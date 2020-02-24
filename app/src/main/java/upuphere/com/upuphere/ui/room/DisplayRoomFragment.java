package upuphere.com.upuphere.ui.room;


import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.MainActivity;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.adapter.PostAdapter;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.databinding.FragmentDisplayRoomBinding;
import upuphere.com.upuphere.fragment.DisplayPhotoFragmentArgs;
import upuphere.com.upuphere.fragment.MoreOptionBottomSheetDialogFragment;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.viewmodel.DisplayRoomViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayRoomFragment extends Fragment implements PostAdapter.PostAdapterListener{


    public DisplayRoomFragment() {
        // Required empty public constructor
    }

    private View rootView;
    private DisplayRoomViewModel viewModel;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    public String roomId;
    private FragmentDisplayRoomBinding binding;
    String roomName;

    List<Post> fetchedPost = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AllRooms room = DisplayRoomFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getRoom();
        roomId = room.getId();
        roomName = room.getRoomName();

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(roomName);

        Log.d("ROOM NAME",roomName);
        Log.d("ROOM ID DISPLAY",roomId);

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_display_room,container,false);
        viewModel = ViewModelProviders.of(requireActivity()).get(DisplayRoomViewModel.class);
        rootView = binding.getRoot();
        binding.setModel(viewModel);

        return rootView;
    }

    PrefManager prefManager;
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        viewModel.setDisplayRoomInterface(new DisplayRoomViewModel.DisplayRoomInterface() {
            @Override
            public void onFabClick() {
                NavDirections action = DisplayRoomFragmentDirections.actionRoomFragmentToCreatePostFragment(roomId);
                Navigation.findNavController(view).navigate(action);
            }

            @Override
            public void onRoomBlocked(String message) {
                binding.emptyStateContainer.setVisibility(View.VISIBLE);
                binding.postRecyclerView.setVisibility(View.GONE);
                binding.emptyStateContainer.setText(message);
                binding.unblockRoomButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClickUnhideRoomButton() {
                Log.d("Display Room","UNHIDE THE ROOM");
                DecodeToken decodeToken = DecodeToken.newInstance();
                decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                    @Override
                    public void onTokenValid() {
                        viewModel.unHideRoom(roomId, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
                                getPostInRoom(roomId);
                            }
                            @Override
                            public void showError(String error) {

                            }
                        });
                    }

                    @Override
                    public void onTokenAllInvalid() {

                    }
                });
                decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
            }
        });

        observeProgressBar();

        initializeRecyclerView();

        setUpSwipeRefreshLayout();

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                getPostInRoom(roomId);
            }
        });
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private void setUpSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                getPostInRoom(roomId);
            }
        });
    }

    private void observeProgressBar() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    binding.progressBar7.bringToFront();
                    binding.progressBar7.setVisibility(View.VISIBLE);
                }else {
                    binding.progressBar7.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initializeRecyclerView() {
        recyclerView = binding.postRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        postAdapter = new PostAdapter(this);
        recyclerView.setAdapter(postAdapter);
    }

    private void getPostInRoom(String roomId) {
        viewModel.getAllPostInRoom(roomId).observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                mSwipeRefreshLayout.setRefreshing(false);

                if(posts != null && posts.size() > 0){
                    binding.postRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyStateContainer.setVisibility(View.GONE);
                    binding.unblockRoomButton.setVisibility(View.GONE);
                    fetchedPost.addAll(posts);
                    postAdapter.setPost(posts);
                }else{
                    binding.emptyStateContainer.setVisibility(View.VISIBLE);
                    binding.postRecyclerView.setVisibility(View.GONE);
                }


            }
        });
    }

    @Override
    public void onCommentClicked(Post post) {
        Toast.makeText(getActivity(),post.getId(),Toast.LENGTH_SHORT).show();

        //NavDirections action = DisplayRoomFragmentDirections.actionRoomFragmentToCommentFragment(post.getId(),fetchedPost);
        Bundle bundle = new Bundle();
        bundle.putString("postId",post.getId());
        bundle.putParcelableArrayList("fetched_post", (ArrayList<? extends Parcelable>) fetchedPost);
        Navigation.findNavController(rootView).navigate(R.id.commentFragment,bundle);
    }

    @Override
    public void onShareClicked(Post post) {
        Toast.makeText(getActivity(), post.getAuthor(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreButtonClicked(Post post) {
        Log.d("Display Fragment","MORE BUTTON CLICKED");
        Log.d("DisplayFragment room id",post.getId());
        Log.d("DisplayFragment user id",post.getAuthor());

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

                            viewModel.blockUserOrHidePost(userId, AppConfig.BLOCK_USER, new StringCallBack() {
                                @Override
                                public void success(String item) {
                                    moreOptionBottomSheetDialogFragment.dismiss();
                                    postAdapter.removePostCreatedByBlockedUser(post.getAuthorUserId());
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
                Log.d("SinglePost post id",post.getId());

                DecodeToken decodeToken = DecodeToken.newInstance();
                decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                    @Override
                    public void onTokenValid() {

                        viewModel.blockUserOrHidePost(post.getId(), AppConfig.HIDE_POST, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                postAdapter.removeHidedPost(post);
                                Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
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

                            viewModel.blockUserOrHidePost(userId, AppConfig.BLOCK_USER, new StringCallBack() {
                                @Override
                                public void success(String item) {
                                    moreOptionBottomSheetDialogFragment.dismiss();
                                    postAdapter.removePostCreatedByBlockedUser(post.getAuthorUserId());
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
        postAdapter.removeAllPost();
    }
}
