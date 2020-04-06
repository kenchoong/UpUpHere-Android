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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import upuphere.com.upuphere.adapter.SinglePostAdapter;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.databinding.FragmentSinglePostBinding;
import upuphere.com.upuphere.fragment.MoreOptionBottomSheetDialogFragment;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.KeyboardHelper;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.viewmodel.SinglePostViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SinglePostFragment extends Fragment implements SinglePostViewModel.SinglePostInterface, SinglePostAdapter.SinglePostAdapterListener {

    FragmentSinglePostBinding binding;
    SinglePostViewModel viewModel;
    View rootView;
    String postId;
    RecyclerView singlePostRecyclerView;
    SinglePostAdapter singlePostAdapter;

    private List<Post> singlePostList = new ArrayList<>();
    private List<CommentModel> commentList = new ArrayList<>();

    public SinglePostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        postId = SinglePostFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getPostId();
        //Toast.makeText(getActivity(),postId,Toast.LENGTH_LONG).show();
        Log.d("SINGLEPOSTFRAGMENT",postId);

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_single_post,container,false);
        viewModel = new ViewModelProvider(requireActivity()).get(SinglePostViewModel.class);
        rootView = binding.getRoot();
        binding.setViewmodel(viewModel);

        return rootView;
    }

    PrefManager prefManager;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        initializeProgressBar();

        viewModel.setCommentInterface(this);

        initialRecyclerView();

        populatePostToRecycleView();

        //populateCommentToRecycleView(singlePostList);

    }

    private void initializeProgressBar() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    binding.loadingBar.bringToFront();
                    binding.loadingBar.setVisibility(View.VISIBLE);
                }
                else{
                    binding.loadingBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initialRecyclerView() {
        singlePostRecyclerView = binding.postAndCommentRecyclerView;
        singlePostRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        singlePostAdapter = new SinglePostAdapter(this);
        singlePostRecyclerView.setAdapter(singlePostAdapter);
    }

    private void populatePostToRecycleView() {
        viewModel.getSinglePostByPostId(postId).observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if(posts != null){
                    binding.postAndCommentRecyclerView.setVisibility(View.VISIBLE);
                    binding.commentFieldContainer.setVisibility(View.VISIBLE);
                    binding.emptyStateContainer.setVisibility(View.GONE);
                    binding.unhideButton.setVisibility(View.GONE);

                    singlePostList.addAll(posts);
                    singlePostAdapter.setPost(posts);
                    populateCommentToRecycleView(posts);
                }else{
                    binding.postAndCommentRecyclerView.setVisibility(View.GONE);
                    binding.commentFieldContainer.setVisibility(View.GONE);
                    binding.emptyStateContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void populateCommentToRecycleView(List<Post> singlePostList) {
        viewModel.getCommentLiveData(singlePostList,postId).observe(getViewLifecycleOwner(), new Observer<List<CommentModel>>() {
            @Override
            public void onChanged(List<CommentModel> comment) {
                singlePostAdapter.setComment(comment);
            }
        });
    }

    @Override
    public void onSend() {
        String commentString = viewModel.commentText;
        if(!TextUtils.isEmpty(commentString)) {
            sendCommentToServer(commentString);

            appendNewCommentToCommentList(commentString);

            binding.commentField.setText("");

            Toast.makeText(getActivity(),"Comment sent",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),"Please enter your comment",Toast.LENGTH_SHORT).show();
        }
    }

    int blockTypeRecord;
    String blockItemIdString;

    @Override
    public void onPostOrUserBlock(String message,int blockType,String blockItemId) {
        blockTypeRecord = blockType;
        blockItemIdString = blockItemId;
        binding.postAndCommentRecyclerView.setVisibility(View.GONE);
        binding.commentFieldContainer.setVisibility(View.GONE);
        binding.emptyStateContainer.setVisibility(View.VISIBLE);
        binding.emptyStateContainer.setText(message);
        binding.unhideButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickUnHideButton() {
        DecodeToken decodeToken = DecodeToken.newInstance();
        decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
            @Override
            public void onTokenValid() {
                if(blockTypeRecord==AppConfig.BLOCK_USER){
                    viewModel.unHideSomething(blockItemIdString,AppConfig.BLOCK_USER, new StringCallBack() {
                        @Override
                        public void success(String item) {
                            Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
                            populatePostToRecycleView();
                        }

                        @Override
                        public void showError(String error) {
                            Toast.makeText(getActivity(), "Error when unhide post", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if(blockTypeRecord==AppConfig.HIDE_POST) {
                    viewModel.unHideSomething(postId,AppConfig.HIDE_POST, new StringCallBack() {
                        @Override
                        public void success(String item) {
                            Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
                            populatePostToRecycleView();
                        }

                        @Override
                        public void showError(String error) {
                            Toast.makeText(getActivity(), "Error when unhide post", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onTokenAllInvalid() {

            }
        });

        decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
    }

    private void appendNewCommentToCommentList(String commentString) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String date = formatter.format(new Date(System.currentTimeMillis()));

        CommentModel comment = new CommentModel();
        comment.setTextComment(commentString);
        comment.setUser(new PrefManager(getActivity()).getUsername());
        comment.setCommenterUserId(new PrefManager(getActivity()).getUserRealId());
        comment.setCreatedAt(date);
        viewModel.appendNewCommentToMutableLiveData(singlePostList,postId,comment);
    }

    private void sendCommentToServer(final String commentString) {
        DecodeToken decodeToken = DecodeToken.newInstance();
        decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
            @Override
            public void onTokenValid() {
                viewModel.createComment(postId,commentString);
            }

            @Override
            public void onTokenAllInvalid() {

            }
        });

        decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        singlePostAdapter.removeAllData();
        hideKeyBoard();
    }

    private void hideKeyBoard(){
        KeyboardHelper.hideKeyboard(getActivity());
    }

    @Override
    public void onPostMoreButtonClick(Post post) {
        Log.d("single Fragment","MORE BUTTON CLICKED");
        Log.d("singleFragment room id",post.getId());
        Log.d("singleFragment user id",post.getAuthor());

        showPostMoreOptionMenu(post);
    }

    private MoreOptionBottomSheetDialogFragment moreOptionBottomSheetDialogFragment;
    private void showPostMoreOptionMenu(final Post post) {
        moreOptionBottomSheetDialogFragment = MoreOptionBottomSheetDialogFragment.newInstance();
        moreOptionBottomSheetDialogFragment.setOnOptionListener(new MoreOptionBottomSheetDialogFragment.OnOptionListener() {
            @Override
            public void onBlockUser() {
                Log.d("Single Block user",post.getAuthorUserId());
                final String userId = post.getAuthorUserId();

                if(prefManager.getUserRealId().equals(userId)){
                    moreOptionBottomSheetDialogFragment.dismiss();
                    Toast.makeText(getActivity(),"Cannot block yourself",Toast.LENGTH_SHORT).show();
                }
                else {

                    DecodeToken decodeToken = DecodeToken.newInstance();
                    decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                        @Override
                        public void onTokenValid() {

                            viewModel.blockUserOrHidePostOrHideComment(userId, null, AppConfig.BLOCK_USER, new StringCallBack() {
                                @Override
                                public void success(String item) {
                                    moreOptionBottomSheetDialogFragment.dismiss();
                                    binding.postAndCommentRecyclerView.setVisibility(View.GONE);
                                    binding.commentFieldContainer.setVisibility(View.GONE);
                                    binding.emptyStateContainer.setVisibility(View.VISIBLE);
                                    binding.emptyStateContainer.setText(getResources().getString(R.string.user_success_block));
                                    blockItemIdString = userId;
                                    blockTypeRecord = AppConfig.BLOCK_USER;
                                    binding.unhideButton.setVisibility(View.VISIBLE);
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
                Log.d("SinglePost post id",post.getId());

                DecodeToken decodeToken = DecodeToken.newInstance();
                decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                    @Override
                    public void onTokenValid() {

                        viewModel.blockUserOrHidePostOrHideComment(postId, null,AppConfig.HIDE_POST, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                binding.postAndCommentRecyclerView.setVisibility(View.GONE);
                                binding.commentFieldContainer.setVisibility(View.GONE);
                                binding.emptyStateContainer.setVisibility(View.VISIBLE);
                                binding.emptyStateContainer.setText(getResources().getString(R.string.post_success_hide));
                                blockItemIdString = postId;
                                blockTypeRecord = AppConfig.HIDE_POST;
                                binding.unhideButton.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void showError(String error) {
                                Log.d("HIDE POST",error);
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
                final String userId = post.getAuthorUserId();

                if(prefManager.getUserRealId().equals(userId)){
                    moreOptionBottomSheetDialogFragment.dismiss();
                    Toast.makeText(getActivity(),"Cannot report yourself",Toast.LENGTH_SHORT).show();
                }
                else {
                    DecodeToken decodeToken = DecodeToken.newInstance();
                    decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                        @Override
                        public void onTokenValid() {

                            viewModel.blockUserOrHidePostOrHideComment(userId, null, AppConfig.BLOCK_USER, new StringCallBack() {
                                @Override
                                public void success(String item) {
                                    moreOptionBottomSheetDialogFragment.dismiss();
                                    binding.postAndCommentRecyclerView.setVisibility(View.GONE);
                                    binding.commentFieldContainer.setVisibility(View.GONE);
                                    binding.emptyStateContainer.setVisibility(View.VISIBLE);
                                    binding.emptyStateContainer.setText(getResources().getString(R.string.user_success_report));
                                    blockItemIdString = userId;
                                    blockTypeRecord = AppConfig.BLOCK_USER;
                                    binding.unhideButton.setVisibility(View.VISIBLE);
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
    public void onCommentMoreButtonClick(CommentModel comment) {
        Log.d("single c Fragment","MORE BUTTON CLICKED");
        Log.d("single c Fragment id",comment.getUser());

        showCommentMoreOptionMenu(comment);
    }

    private void showCommentMoreOptionMenu(final CommentModel comment) {
        moreOptionBottomSheetDialogFragment = MoreOptionBottomSheetDialogFragment.newInstance();
        moreOptionBottomSheetDialogFragment.setOnOptionListener(new MoreOptionBottomSheetDialogFragment.OnOptionListener() {
            @Override
            public void onBlockUser() {
                Log.d("Comment Block user",comment.getCommenterUserId());

                final String userId = comment.getCommenterUserId();
                DecodeToken decodeToken = DecodeToken.newInstance();
                decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                    @Override
                    public void onTokenValid() {

                        viewModel.blockUserOrHidePostOrHideComment(userId,null, AppConfig.BLOCK_USER, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                singlePostAdapter.removeCommentCreatedByBlockedUser(comment.getCommenterUserId());
                                Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void showError(String error) {
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
            public void onHide() {
                DecodeToken decodeToken = DecodeToken.newInstance();
                decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                    @Override
                    public void onTokenValid() {

                        viewModel.blockUserOrHidePostOrHideComment(comment.getCommentId(),postId, AppConfig.HIDE_COMMENT, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                singlePostAdapter.removeHidedComment(comment);
                                Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void showError(String error) {
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
                final String userId = comment.getCommenterUserId();
                DecodeToken decodeToken = DecodeToken.newInstance();
                decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                    @Override
                    public void onTokenValid() {

                        viewModel.blockUserOrHidePostOrHideComment(userId,null, AppConfig.BLOCK_USER, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                singlePostAdapter.removeCommentCreatedByBlockedUser(comment.getCommenterUserId());
                                Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void showError(String error) {
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
            public void onCancel() {
                moreOptionBottomSheetDialogFragment.dismiss();
            }
        });

        moreOptionBottomSheetDialogFragment.show(Objects.requireNonNull(getFragmentManager()),MoreOptionBottomSheetDialogFragment.TAG);
    }
}
