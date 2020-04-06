package upuphere.com.upuphere.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.adapter.CommentAdapter;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.databinding.FragmentCommentBinding;
import upuphere.com.upuphere.fragment.MoreOptionBottomSheetDialogFragment;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.KeyboardHelper;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.repositories.PostRepo;
import upuphere.com.upuphere.repositories.RoomRepo;
import upuphere.com.upuphere.viewmodel.CommentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment implements CommentViewModel.CommentInterface, CommentAdapter.CommentAdapterListner {


    public CommentFragment() {
        // Required empty public constructor
    }


    FragmentCommentBinding binding;
    View rootView;
    CommentViewModel commentViewModel;
    RecyclerView commentRecyclerView;
    CommentAdapter commentAdapter;

    List<Post> postList;
    String postId;
    PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        postId = getArguments().getString("postId");
        postList = getArguments().getParcelableArrayList("fetched_post");

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_comment,container,false);
        commentViewModel = new ViewModelProvider(requireActivity()).get(CommentViewModel.class);
        rootView = binding.getRoot();
        binding.setViewmodel(commentViewModel);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        commentViewModel.setCommentInterface(this);

        observeProgressBar();

        initializeRecyclerView();

        populateCommentIntoRecyclerView();
    }

    private void observeProgressBar() {
        commentViewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    binding.progressBar8.bringToFront();
                    binding.progressBar8.setVisibility(View.VISIBLE);
                }else {
                    binding.progressBar8.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initializeRecyclerView() {
        commentRecyclerView = binding.commentRecyclerView;
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        commentAdapter = new CommentAdapter(this);
        commentRecyclerView.setAdapter(commentAdapter);
    }


    private void populateCommentIntoRecyclerView() {
        commentViewModel.getCommentLiveData(postList,postId).observe(getViewLifecycleOwner(), new Observer<List<CommentModel>>() {
            @Override
            public void onChanged(List<CommentModel> commentModels) {
                if(commentModels.size()> 0){
                    binding.emptyStateComment.setVisibility(View.GONE);
                    commentAdapter.setComment(commentModels);
                    commentRecyclerView.smoothScrollToPosition(commentModels.size() - 1);

                }else{
                    binding.emptyStateComment.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onSend() {
        String commentString = commentViewModel.commentText;
        if(!TextUtils.isEmpty(commentString)) {
            sendCommentToServer(commentString);

            appendNewCommentToCommentList(commentString);

            binding.commentField.setText("");
        }else{
            Toast.makeText(getActivity(),"Please enter your comment",Toast.LENGTH_SHORT).show();
        }

    }

    private void appendNewCommentToCommentList(String commentString) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
        String date = formatter.format(new Date(System.currentTimeMillis()));

        CommentModel comment = new CommentModel();
        comment.setTextComment(commentString);
        comment.setUser(new PrefManager(getActivity()).getUsername());
        comment.setCommenterUserId(new PrefManager(getActivity()).getUserRealId());
        comment.setCreatedAt(date);
        commentViewModel.appendNewCommentToMutableLiveData(postList,postId,comment);
    }

    private void sendCommentToServer(final String commentString) {
        DecodeToken decodeToken = DecodeToken.newInstance();
        decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
            @Override
            public void onTokenValid() {
                commentViewModel.createComment(postId, commentString);
            }

            @Override
            public void onTokenAllInvalid() {

            }
        });

        decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
    }


    @Override
    public void onMoreButtonClicked(CommentModel comment) {
        Log.d("Comment Fragment","MORE BUTTON CLICKED");
        Log.d("Comment Fragment id",comment.getUser());
        //Log.d("Main Fragment user id",comment.getCreatedBy());

        showCommentMoreOptionMenu(comment);
    }

    private MoreOptionBottomSheetDialogFragment moreOptionBottomSheetDialogFragment;

    private void showCommentMoreOptionMenu(final CommentModel comment) {

        moreOptionBottomSheetDialogFragment = MoreOptionBottomSheetDialogFragment.newInstance();
        moreOptionBottomSheetDialogFragment.setOnOptionListener(new MoreOptionBottomSheetDialogFragment.OnOptionListener() {
            @Override
            public void onBlockUser() {
                Log.d("Comment Block user", comment.getCommenterUserId());

                final String userId = comment.getCommenterUserId();
                if (prefManager.getUserRealId().equals(userId)) {
                    moreOptionBottomSheetDialogFragment.dismiss();
                    Toast.makeText(getActivity(), "Cannot block yourself", Toast.LENGTH_SHORT).show();
                } else {
                    DecodeToken decodeToken = DecodeToken.newInstance();
                    decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                        @Override
                        public void onTokenValid() {

                            commentViewModel.blockUserOrHideComment(userId, null, AppConfig.BLOCK_USER, new StringCallBack() {
                                @Override
                                public void success(String item) {
                                    moreOptionBottomSheetDialogFragment.dismiss();
                                    commentAdapter.removeCommentCreatedByBlockedUser(comment.getCommenterUserId());
                                    Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void showError(String error) {
                                    moreOptionBottomSheetDialogFragment.dismiss();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onTokenAllInvalid() {

                        }
                    });
                    decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
                }
            }

            @Override
            public void onHide() {
                Log.d("Comment Block commentId",comment.getCommentId());

                DecodeToken decodeToken = DecodeToken.newInstance();
                decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                    @Override
                    public void onTokenValid() {

                        commentViewModel.blockUserOrHideComment(comment.getCommentId(),postId, AppConfig.HIDE_COMMENT, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                commentAdapter.removeHidedComment(comment);
                                Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void showError(String error) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onTokenAllInvalid() {

                    }
                });
                decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
            }

            @Override
            public void onReport() {
                Log.d("Comment Block user",comment.getCommenterUserId());

                final String userId = comment.getCommenterUserId();
                if(prefManager.getUserRealId().equals(userId)){
                    moreOptionBottomSheetDialogFragment.dismiss();
                    Toast.makeText(getActivity(),"Cannot report yourself",Toast.LENGTH_SHORT).show();
                }
                else {
                    DecodeToken decodeToken = DecodeToken.newInstance();
                    decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                        @Override
                        public void onTokenValid() {

                            commentViewModel.blockUserOrHideComment(userId, null, AppConfig.BLOCK_USER, new StringCallBack() {
                                @Override
                                public void success(String item) {
                                    moreOptionBottomSheetDialogFragment.dismiss();
                                    commentAdapter.removeCommentCreatedByBlockedUser(comment.getCommenterUserId());
                                    Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void showError(String error) {
                                    moreOptionBottomSheetDialogFragment.dismiss();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onTokenAllInvalid() {

                        }
                    });
                    decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
                }
            }

            @Override
            public void onCancel() {
                moreOptionBottomSheetDialogFragment.dismiss();
            }
        });

        moreOptionBottomSheetDialogFragment.show(Objects.requireNonNull(getFragmentManager()),MoreOptionBottomSheetDialogFragment.TAG);
    }

    @Override
    public void onStop() {
        super.onStop();
        commentAdapter.removeAllComment();
        hideKeyBoard();
    }

    private void hideKeyBoard(){
        KeyboardHelper.hideKeyboard(getActivity());
    }
}
