package upuphere.com.upuphere;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.databinding.FragmentMainBinding;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.viewmodel.MainViewModel;


public class MainFragment extends Fragment {
    public MainFragment() {
        // Required empty public constructor
    }

    MainViewModel mainViewModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        // Inflate the layout for this fragment
        FragmentMainBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false);
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        View view = binding.getRoot();
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
    }
}
