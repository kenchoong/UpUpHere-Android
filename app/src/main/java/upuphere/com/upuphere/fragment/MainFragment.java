package upuphere.com.upuphere.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.repositories.UserRepo;


public class MainFragment extends Fragment {

    PrefManager prefManager;
    Button logoutButton;

    UserRepo userRepo = UserRepo.getInstance();
    public MainFragment() {
        // Required empty public constructor
    }

    SharedPreferenceBooleanLiveData sharedPreferenceBooleanLiveData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Button myButton = view.findViewById(R.id.button);
        Button button2 = view.findViewById(R.id.button123);
        logoutButton = view.findViewById(R.id.logoutButton);

        prefManager = new PrefManager(getActivity());

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action =
                        MainFragmentDirections.actionMainFragmentToCreateRoomFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prefManager.removeAllSharedPrefrences();
                prefManager.setIsLoggedIn(false);

                NavController navController = Navigation.findNavController(view);
                navController.popBackStack(R.id.mainFragment,true);
                navController.navigate(R.id.loginFragment);
/*
                String accessToken = prefManager.getUserAccessToken();

                if(accessToken != null){
                    userRepo.revokedAccessToken(new CommonCallBack() {
                        @Override
                        public void success() {
                            revokeRefreshToken();
                        }

                        @Override
                        public void showError(String error) {

                        }
                    });
                }
*/
            }
        });

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
    }

    private void revokeRefreshToken(){
        userRepo.revokeRefreshToken(new CommonCallBack() {
            @Override
            public void success() {
                prefManager.removeAllSharedPrefrences();
                prefManager.setIsLoggedIn(false);
                //startActivity(new Intent(getActivity(), LoginActivity.class));
            }

            @Override
            public void showError(String error) {

            }
        });
    }

}
