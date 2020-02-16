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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.adapter.SinglePostAdapter;
import upuphere.com.upuphere.databinding.FragmentSinglePostBinding;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.viewmodel.SinglePostViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SinglePostFragment extends Fragment implements SinglePostViewModel.SinglePostInterface{

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        postId = SinglePostFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getPostId();
        Toast.makeText(getActivity(),postId,Toast.LENGTH_LONG).show();

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_single_post,container,false);
        viewModel = ViewModelProviders.of(requireActivity()).get(SinglePostViewModel.class);
        rootView = binding.getRoot();
        binding.setViewmodel(viewModel);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.setCommentInterface(this);

        initialRecyclerView();

        populatePostToRecycleView();
    }

    private void initialRecyclerView() {
        singlePostRecyclerView = binding.postAndCommentRecyclerView;
        singlePostRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        singlePostAdapter = new SinglePostAdapter();
        singlePostRecyclerView.setAdapter(singlePostAdapter);
    }


    private void populatePostToRecycleView() {
        Log.d("SINGLE POST","POST NOT NULL NOW");
        viewModel.getSinglePostByPostId(postId).observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if(posts != null){
                    Log.d("SINGLE POST","POST NOT NULL NOW");
                    singlePostAdapter.setPost(posts);
                    populateCommentToRecycleView(posts);
                }else{
                    Log.d("SINGLE POST","POST IS NULL NOW");
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
        appendNewCommentToCommentList();

        binding.commentField.setText("");

        sendCommentToServer();
    }

    private void appendNewCommentToCommentList() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String date = formatter.format(new Date(System.currentTimeMillis()));

        CommentModel comment = new CommentModel();
        comment.setTextComment(viewModel.commentText);
        comment.setUser(new PrefManager(getActivity()).getUserId());
        comment.setCreatedAt(date);
        viewModel.appendNewCommentToMutableLiveData(singlePostList,postId,comment);
    }

    private void sendCommentToServer() {
        DecodeToken decodeToken = DecodeToken.newInstance();
        decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
            @Override
            public void onTokenValid() {
                viewModel.createComment(postId,viewModel.commentText);
            }

            @Override
            public void onTokenAllInvalid() {

            }
        });

        decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
    }
}
