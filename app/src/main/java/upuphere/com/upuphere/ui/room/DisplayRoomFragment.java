package upuphere.com.upuphere.ui.room;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.adapter.PostAdapter;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.databinding.FragmentDisplayRoomBinding;
import upuphere.com.upuphere.fragment.MoreOptionBottomSheetDialogFragment;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.models.PostAdsData;
import upuphere.com.upuphere.viewmodel.DisplayRoomViewModel;

import static com.google.android.gms.ads.formats.NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT;

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
    //private PostAdapter postAdapter;
    public String roomId;
    private FragmentDisplayRoomBinding binding;
    String roomName;

    List<Post> fetchedPost = new ArrayList<>();
    AllRooms room;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        room = DisplayRoomFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getRoom();
        roomId = room.getId();
        roomName = room.getRoomName();

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(roomName);

        Log.d("ROOM NAME",roomName);
        Log.d("ROOM ID DISPLAY",roomId);

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_display_room,container,false);
        viewModel = new ViewModelProvider(requireActivity()).get(DisplayRoomViewModel.class);
        rootView = binding.getRoot();
        binding.setModel(viewModel);

        setHasOptionsMenu(true);
        return rootView;
    }

    PrefManager prefManager;

    int blockTypeRecord;
    String blockItemIdString;

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
            public void onRoomOrUserBlocked(String message,int blockType,String blockItemId){
                blockTypeRecord = blockType;
                blockItemIdString = blockItemId;
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
                        if(blockTypeRecord==AppConfig.BLOCK_USER){
                            viewModel.unHideSomething(blockItemIdString,AppConfig.BLOCK_USER, new StringCallBack() {
                                @Override
                                public void success(String item) {
                                    Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
                                    getPostInRoom(roomId);
                                }

                                @Override
                                public void showError(String error) {
                                    Toast.makeText(getActivity(), "Error when unhide post", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        if(blockTypeRecord==AppConfig.HIDE_ROOM) {
                            viewModel.unHideSomething(blockItemIdString,AppConfig.HIDE_ROOM, new StringCallBack() {
                                @Override
                                public void success(String item) {
                                    Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
                                    getPostInRoom(roomId);
                                }

                                @Override
                                public void showError(String error) {
                                    Toast.makeText(getActivity(), "Error when unhide room", Toast.LENGTH_LONG).show();
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

    PostAdapter postAdapter;
    private void initializeRecyclerView() {
        recyclerView = binding.postRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        //postAdapter = new PostAdapter(this);
        postAdapter = new PostAdapter(this);
        recyclerView.setAdapter(postAdapter);
    }


    private void getPostInRoom(String roomId) {
        viewModel.getAllPostInRoom(roomId).observe(getViewLifecycleOwner(), new Observer<List<Post>>() {

            @Override
            public void onChanged(List<Post> posts) {

                if(posts != null) {
                    if (posts.size() > 0) {

                        binding.postRecyclerView.setVisibility(View.VISIBLE);
                        binding.emptyStateContainer.setVisibility(View.GONE);
                        binding.unblockRoomButton.setVisibility(View.GONE);
                        fetchedPost.addAll(posts);

                        loadNativeAds(posts);
                    }else{
                        mSwipeRefreshLayout.setRefreshing(false);
                        binding.emptyStateContainer.setVisibility(View.VISIBLE);
                        binding.postRecyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private List<PostAdsData> mergeData(List<Post>posts, List<UnifiedNativeAd> ads){
        List<PostAdsData> postAdsDataList = new ArrayList<>();

        for(UnifiedNativeAd ad : ads) {
            PostAdsData data = new PostAdsData();
            data.ads = ad;
            data.post = null;
            data.type = 1;
            postAdsDataList.add(data);
        }


        for(Post item : posts) {
            PostAdsData data = new PostAdsData();
            data.ads = null;
            data.post = item;
            data.type = 2;
            postAdsDataList.add(data);
        }

        return postAdsDataList;
    }

    private void displayContent(List<Post> posts,List<UnifiedNativeAd> ads){
        mSwipeRefreshLayout.setRefreshing(false);
        List<PostAdsData> postAdsDataForRecyclerView = mergeData(posts,ads);
        postAdapter.setPostAdsDataList(postAdsDataForRecyclerView);
    }

    AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private void loadNativeAds(final List<Post> posts) {

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .setMediaAspectRatio(NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT)
                .build();

        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getResources().getString(R.string.admob_post_fragment_native_ads));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.

                        if (!adLoader.isLoading()) {
                            mNativeAds.clear();
                            mNativeAds.add(unifiedNativeAd);

                            displayContent(posts,mNativeAds);
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            displayContent(posts,mNativeAds);
                        }
                    }
                }).withNativeAdOptions(adOptions)
                .build();

        // Load the Native ads.
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onCommentClicked(Post post) {
        //Toast.makeText(getActivity(),post.getId(),Toast.LENGTH_SHORT).show();

        //NavDirections action = DisplayRoomFragmentDirections.actionRoomFragmentToCommentFragment(post.getId(),fetchedPost);
        Bundle bundle = new Bundle();
        bundle.putString("postId",post.getId());
        bundle.putParcelableArrayList("fetched_post", (ArrayList<? extends Parcelable>) fetchedPost);
        Navigation.findNavController(rootView).navigate(R.id.commentFragment,bundle);
    }

    @Override
    public void onShareClicked(Post post) {
        //Toast.makeText(getActivity(), post.getAuthor(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreButtonClicked(Post post, int position) {
        Log.d("Display Fragment","MORE BUTTON CLICKED");
        Log.d("DisplayFragment room id",post.getId());
        Log.d("DisplayFragment user id",post.getAuthor());

        showPostMoreOptionMenu(post,position);
    }

    private MoreOptionBottomSheetDialogFragment moreOptionBottomSheetDialogFragment;
    private void showPostMoreOptionMenu(final Post post, final int position) {
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
                                    //Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();

                                    showSnackBar(item,AppConfig.BLOCK_USER,post.getAuthorUserId());
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
                                postAdapter.removeHidedPost(position);

                                showSnackBar(item,AppConfig.HIDE_POST,post.getId());
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
                                    showSnackBar(item,AppConfig.BLOCK_USER,post.getAuthorUserId());
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

    private void showRoomMoreOptionMenu(final AllRooms room){
        moreOptionBottomSheetDialogFragment = MoreOptionBottomSheetDialogFragment.newInstance();
        moreOptionBottomSheetDialogFragment.setOnOptionListener(new MoreOptionBottomSheetDialogFragment.OnOptionListener() {
            @Override
            public void onBlockUser() {
                final String userId = room.getRoomOwnerUserId();

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
                                    binding.emptyStateContainer.setVisibility(View.VISIBLE);
                                    binding.emptyStateContainer.setText(item);
                                    binding.postRecyclerView.setVisibility(View.GONE);
                                    blockItemIdString = userId;
                                    blockTypeRecord = AppConfig.BLOCK_USER;
                                    binding.unblockRoomButton.setVisibility(View.VISIBLE);

                                    showSnackBar(item,AppConfig.BLOCK_USER,room.getRoomOwnerUserId());
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

                DecodeToken decodeToken = DecodeToken.newInstance();
                decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                    @Override
                    public void onTokenValid() {

                        viewModel.blockUserOrHidePost(roomId, AppConfig.HIDE_ROOM, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                binding.emptyStateContainer.setVisibility(View.VISIBLE);
                                binding.emptyStateContainer.setText(item);
                                binding.postRecyclerView.setVisibility(View.GONE);
                                blockItemIdString = roomId;
                                blockTypeRecord = AppConfig.HIDE_ROOM;
                                binding.unblockRoomButton.setVisibility(View.VISIBLE);

                                showSnackBar(item,AppConfig.HIDE_ROOM,roomId);
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

            @Override
            public void onReport() {
                final String userId = room.getRoomOwnerUserId();

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
                                    binding.emptyStateContainer.setVisibility(View.VISIBLE);
                                    binding.emptyStateContainer.setText(item);
                                    binding.postRecyclerView.setVisibility(View.GONE);
                                    blockItemIdString = userId;
                                    blockTypeRecord = AppConfig.BLOCK_USER;
                                    binding.unblockRoomButton.setVisibility(View.VISIBLE);

                                    showSnackBar(item,AppConfig.BLOCK_USER,room.getRoomOwnerUserId());
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_more_option,menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.moreMenuOption:
                if(room != null){
                    showRoomMoreOptionMenu(room);
                }

                return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSnackBar(String message, final int blockType, final String blockItemId){
        final Snackbar snackbar = Snackbar.make(binding.rootLayout,message, BaseTransientBottomBar.LENGTH_SHORT);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.unHideSomething(blockItemId, blockType, new StringCallBack() {
                    @Override
                    public void success(String item) {
                        snackbar.dismiss();
                        Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
                        getPostInRoom(roomId);
                    }

                    @Override
                    public void showError(String error) {
                        snackbar.dismiss();
                        Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        snackbar.show();
    }


    @Override
    public void onStop() {
        super.onStop();
        postAdapter.removeAllPost();
        //postAdapter.postAdsDataList = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //postAdapter.postAdsDataList = null;
    }
}
