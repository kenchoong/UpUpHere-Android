package upuphere.com.upuphere.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.adapter.CommentAdapter;
import upuphere.com.upuphere.databinding.FragmentCommentBinding;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.repositories.PostRepo;
import upuphere.com.upuphere.repositories.RoomRepo;
import upuphere.com.upuphere.viewmodel.CommentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment implements CommentViewModel.CommentInterface {


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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        postId = getArguments().getString("postId");
        postList = getArguments().getParcelableArrayList("fetched_post");

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_comment,container,false);
        commentViewModel = ViewModelProviders.of(requireActivity()).get(CommentViewModel.class);
        rootView = binding.getRoot();
        binding.setViewmodel(commentViewModel);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        commentViewModel.setCommentInterface(this);

        initializeRecyclerView();

        populateCommentIntoRecyclerView();
    }

    private void initializeRecyclerView() {
        commentRecyclerView = binding.commentRecyclerView;
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        commentAdapter = new CommentAdapter();
        commentRecyclerView.setAdapter(commentAdapter);
    }


    private void populateCommentIntoRecyclerView() {
        commentViewModel.getCommentLiveData(postList,postId).observe(getViewLifecycleOwner(), new Observer<List<CommentModel>>() {
            @Override
            public void onChanged(List<CommentModel> commentModels) {
                commentAdapter.setComment(commentModels);
                if(commentModels.size() > 0) {
                    commentRecyclerView.smoothScrollToPosition(commentModels.size() - 1);
                }
            }
        });
    }

    @Override
    public void onSend() {

        appendNewCommentToCommentList();

        binding.commentField.setText("");

        sendCommentToServer();
    }

    private void appendNewCommentToCommentList() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
        String date = formatter.format(new Date(System.currentTimeMillis()));

        CommentModel comment = new CommentModel();
        comment.setTextComment(commentViewModel.commentText);
        comment.setUser(new PrefManager(getActivity()).getUserId());
        comment.setCreatedAt(date);
        commentViewModel.appendNewCommentToMutableLiveData(postList,postId,comment);
    }

    private void sendCommentToServer() {
        DecodeToken decodeToken = DecodeToken.newInstance();
        decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
            @Override
            public void onTokenValid() {
                commentViewModel.createComment(postId, commentViewModel.commentText);
            }

            @Override
            public void onTokenAllInvalid() {

            }
        });

        decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
    }


}
