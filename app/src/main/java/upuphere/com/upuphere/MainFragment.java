package upuphere.com.upuphere;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.adapter.RoomAdapter;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.databinding.FragmentMainBinding;
import upuphere.com.upuphere.fragment.MoreOptionBottomSheetDialogFragment;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.SpacingItemDecoration;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.NotificationModel;
import upuphere.com.upuphere.models.RoomAdsData;
import upuphere.com.upuphere.viewmodel.MainViewModel;
import upuphere.com.upuphere.viewmodel.NotificationViewModel;


public class MainFragment extends Fragment implements RoomAdapter.RoomAdapterListener{
    public MainFragment() {
        // Required empty public constructor
    }

    private MainViewModel mainViewModel;
    private FragmentMainBinding binding;
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private View view;
    private NotificationViewModel notificationViewModel;
    SwipeRefreshLayout mSwipeRreshLayout;
    PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        view = binding.getRoot();
        binding.setViewmodel(mainViewModel);

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        observeProgressBar();

        initRecyclerView();

        setUpSwipeRefreshLayout();

        prefManager = new PrefManager(getActivity());
        if(!prefManager.isUserAgreeTerm()){
            NavDirections directions = MainFragmentDirections.actionMainFragmentToWelcomeFragment();
            NavOptions options = new NavOptions.Builder().setPopUpTo(R.id.mainFragment,true).build();
            Navigation.findNavController(view).navigate(directions,options);

        }

        SharedPreferences sharedPreferences = prefManager.getPref();
        SharedPreferenceBooleanLiveData sharedPreferenceBooleanLiveData = new SharedPreferenceBooleanLiveData(sharedPreferences, PrefManager.IS_LOGGED_IN, false);

        sharedPreferenceBooleanLiveData.getBooleanLiveData(PrefManager.IS_LOGGED_IN, false).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoggedIn) {
                Log.d("LOGIN 2", String.valueOf(isLoggedIn));
                if (!isLoggedIn) {
                    NavController navController = Navigation.findNavController(view);
                    navController.popBackStack(R.id.mainFragment,true);
                    navController.navigate(R.id.loginFragment);
                }else{
                    mSwipeRreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRreshLayout.setRefreshing(true);
                            getRoomList();
                        }
                    });
                }
            }
        });


        mainViewModel.setMainFragmentInterface(new MainViewModel.MainFragmentInterface() {
            @Override
            public void onFabClick() {
                NavDirections action = MainFragmentDirections.actionMainFragmentToCreateRoomFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

    }

    private void observeProgressBar() {
        mainViewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    binding.progressBar6.bringToFront();
                    binding.progressBar6.setVisibility(View.VISIBLE);
                }else {
                    binding.progressBar6.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = binding.roomRecyclerView;
        GridLayoutManager.SpanSizeLookup onSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return roomAdapter.getItemViewType(position) == RoomAdapter.UNIFIED_ADS ? 2 : 1;
            }
        };

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(),2);
        mGridLayoutManager.setSpanSizeLookup(onSpanSizeLookup);
        recyclerView.setLayoutManager(mGridLayoutManager);

        recyclerView.addItemDecoration(new SpacingItemDecoration(3, dpToPx(4), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        roomAdapter = new RoomAdapter(this);
        recyclerView.setAdapter(roomAdapter);

    }

    private void getRoomList(){
        mainViewModel.getRoomList().observe(getViewLifecycleOwner(), new Observer<List<AllRooms>>() {
            @Override
            public void onChanged(List<AllRooms> rooms) {
                //mSwipeRreshLayout.setRefreshing(false);
                if(rooms != null && rooms.size() > 0){
                    //roomAdapter.setRoomList(rooms);
                    binding.roomRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyStateRoom.setVisibility(View.GONE);

                    loadNativeAds(rooms);
                }else{
                    binding.roomRecyclerView.setVisibility(View.GONE);
                    binding.emptyStateRoom.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private List<RoomAdsData> mergeData(List<AllRooms>rooms, List<UnifiedNativeAd> ads){
        List<RoomAdsData> roomAdsDataList = new ArrayList<>();

        for(UnifiedNativeAd ad: ads){
            RoomAdsData data = new RoomAdsData();
            data.ads = ad;
            data.rooms = null;
            data.type = 1; // 1 for native ads

            roomAdsDataList.add(0,data);
        }

        for(AllRooms room : rooms){
            RoomAdsData data = new RoomAdsData();
            data.ads = null;
            data.rooms = room;
            data.type = 2;

            roomAdsDataList.add(data);
        }

        return roomAdsDataList;
    }

    private void displayContent(List<AllRooms> rooms, List<UnifiedNativeAd> mNativeAds) {
        mSwipeRreshLayout.setRefreshing(false);
        List<RoomAdsData> roomAdsDataForRecyclerView = mergeData(rooms,mNativeAds);
        roomAdapter.setRoomAdsDataList(roomAdsDataForRecyclerView);
    }

    AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private void loadNativeAds(final List<AllRooms> rooms) {
        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getResources().getString(R.string.admob_test_ads));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.

                        if (!adLoader.isLoading()) {
                            mNativeAds.clear();
                            mNativeAds.add(unifiedNativeAd);

                            displayContent(rooms,mNativeAds);
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
                            displayContent(rooms,mNativeAds);
                        }
                    }
                }).build();

        // Load the Native ads.
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void setUpSwipeRefreshLayout(){
        mSwipeRreshLayout = binding.swipeRefreshLayout;
        mSwipeRreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRreshLayout.setRefreshing(true);
                getRoomList();
            }
        });
    }

    @Override
    public void onRoomClicked(AllRooms room) {
        //Toast.makeText(getActivity(),room.getId(),Toast.LENGTH_SHORT).show();
        NavDirections action = MainFragmentDirections.actionMainFragmentToRoomFragment(room);
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onMoreButtonClicked(AllRooms rooms,int position) {
        Log.d("Main Fragment","MORE BUTTON CLICKED");
        Log.d("Main Fragment room id",rooms.getId());
        Log.d("Main Fragment user id",rooms.getCreatedBy());

        showRoomMoreOptionMenu(rooms,position);
    }


    private MoreOptionBottomSheetDialogFragment moreOptionBottomSheetDialogFragment;
    private void showRoomMoreOptionMenu(final AllRooms rooms, final int position) {
        moreOptionBottomSheetDialogFragment = MoreOptionBottomSheetDialogFragment.newInstance();
        moreOptionBottomSheetDialogFragment.setOnOptionListener(new MoreOptionBottomSheetDialogFragment.OnOptionListener() {
            @Override
            public void onBlockUser() {
                Log.d("Main Room Block user",rooms.getRoomOwnerUserId());
                final String roomOwnerUserId = rooms.getRoomOwnerUserId();

                if(prefManager.getUserRealId().equals(roomOwnerUserId)){
                    moreOptionBottomSheetDialogFragment.dismiss();
                    Toast.makeText(getActivity(),"Cannot block yourself",Toast.LENGTH_SHORT).show();
                }
                else {
                    DecodeToken decodeToken = DecodeToken.newInstance();
                    decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                        @Override
                        public void onTokenValid() {

                            mainViewModel.blockUserOrHideRoom(roomOwnerUserId, AppConfig.BLOCK_USER, new StringCallBack() {
                                @Override
                                public void success(String item) {
                                    moreOptionBottomSheetDialogFragment.dismiss();
                                    roomAdapter.removeRoomCreatedByBlockedUser(roomOwnerUserId);
                                    //Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();

                                    showSnackBar(item,AppConfig.BLOCK_USER,roomOwnerUserId);
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
                        mainViewModel.blockUserOrHideRoom(rooms.getId(), AppConfig.HIDE_ROOM, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                roomAdapter.removeHidedRoom(position);
                                //Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();

                                showSnackBar(item,AppConfig.HIDE_ROOM,rooms.getId());

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
                DecodeToken decodeToken = DecodeToken.newInstance();
                decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                    @Override
                    public void onTokenValid() {
                        mainViewModel.blockUserOrHideRoom(rooms.getId(), AppConfig.HIDE_ROOM, new StringCallBack() {
                            @Override
                            public void success(String item) {
                                moreOptionBottomSheetDialogFragment.dismiss();
                                roomAdapter.removeHidedRoom(position);
                                //Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();

                                showSnackBar(item,AppConfig.HIDE_ROOM,rooms.getId());
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
            public void onCancel() {
                moreOptionBottomSheetDialogFragment.dismiss();
            }
        });

        moreOptionBottomSheetDialogFragment.show(Objects.requireNonNull(getFragmentManager()),MoreOptionBottomSheetDialogFragment.TAG);
    }

    private void showSnackBar(String message, final int blockType, final String blockItemId){
        final Snackbar snackbar = Snackbar.make(binding.rootLayout,message, BaseTransientBottomBar.LENGTH_SHORT);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.unHideSomething(blockItemId, blockType, new StringCallBack() {
                    @Override
                    public void success(String item) {
                        snackbar.dismiss();
                        Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show();
                        getRoomList();
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

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private TextView notificationCount;



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notification,menu);

        final MenuItem menuItem = menu.findItem(R.id.action_notification);

        View actionView = MenuItemCompat.getActionView(menuItem);
        notificationCount = actionView.findViewById(R.id.notification_badge);

        getNotification();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItem);
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getNotification() {
        notificationViewModel.getNotificationList().observe(getViewLifecycleOwner(), new Observer<List<NotificationModel>>() {
            @Override
            public void onChanged(List<NotificationModel> notificationModels) {
                setupBadge(notificationModels.size());

                Log.d("Notification" , String.valueOf(notificationModels.size()));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_notification:
                Log.d("Notification", "Clicked");
                NavDirections action = MainFragmentDirections.actionMainFragmentToNotificationFragment();
                Navigation.findNavController(view).navigate(action);
                return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupBadge(int mNotificationCount){
        if (notificationCount != null) {
            if (mNotificationCount == 0) {
                if (notificationCount.getVisibility() != View.GONE) {
                    notificationCount.setVisibility(View.GONE);
                }
            } else {
                Log.d("NOTIFICATION 12345",String.valueOf(mNotificationCount));
                notificationCount.setText(String.valueOf(Math.min(mNotificationCount, 99)));
                if (notificationCount.getVisibility() != View.VISIBLE) {
                    notificationCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
