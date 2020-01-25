package upuphere.com.upuphere;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.adapter.RoomAdapter;
import upuphere.com.upuphere.databinding.FragmentMainBinding;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.SpacingItemDecoration;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.viewmodel.MainViewModel;


public class MainFragment extends Fragment implements RoomAdapter.RoomAdapterListener{
    public MainFragment() {
        // Required empty public constructor
    }

    private MainViewModel mainViewModel;
    private FragmentMainBinding binding;
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false);
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        view = binding.getRoot();
        binding.setViewmodel(mainViewModel);

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
                if(!isLoggedIn){
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.loginFragment);
                }
            }
        });


        mainViewModel.mainFragmentState.observe(getViewLifecycleOwner(), new Observer<MainViewModel.MainFragmentState>() {
            @Override
            public void onChanged(MainViewModel.MainFragmentState mainFragmentState) {
                switch (mainFragmentState){
                    case MOVE_TO_CREATE_A_ROOM:
                        NavDirections action = MainFragmentDirections.actionMainFragmentToCreateRoomFragment();
                        Navigation.findNavController(view).navigate(action);
                        break;
                }
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
}
