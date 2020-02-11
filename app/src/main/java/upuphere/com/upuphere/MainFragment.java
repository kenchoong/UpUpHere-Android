package upuphere.com.upuphere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.adapter.RoomAdapter;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.databinding.FragmentMainBinding;
import upuphere.com.upuphere.helper.NotificationUtils;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.SpacingItemDecoration;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.NotificationModel;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false);
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        notificationViewModel = ViewModelProviders.of(requireActivity()).get(NotificationViewModel.class);
        view = binding.getRoot();
        binding.setViewmodel(mainViewModel);

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = new PrefManager(getActivity()).getPref();
        SharedPreferenceBooleanLiveData sharedPreferenceBooleanLiveData = new SharedPreferenceBooleanLiveData(sharedPreferences,PrefManager.IS_LOGGED_IN,false);

        sharedPreferenceBooleanLiveData.getBooleanLiveData(PrefManager.IS_LOGGED_IN,false).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoggedIn) {
                Log.d("LOGIN 2",String.valueOf(isLoggedIn));
                if(!isLoggedIn){
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.loginFragment);
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

        initRecyclerView();

        getRoomList();


    }

    private void initRecyclerView() {
        recyclerView = binding.roomRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
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
                roomAdapter.setRoomList(rooms);
            }
        });
    }

    @Override
    public void onRoomClicked(AllRooms room) {
        Toast.makeText(getActivity(),room.getId(),Toast.LENGTH_SHORT).show();
        NavDirections action = MainFragmentDirections.actionMainFragmentToRoomFragment(room);
        Navigation.findNavController(view).navigate(action);
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
