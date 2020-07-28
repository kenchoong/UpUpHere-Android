package upuphere.com.upuphere;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.repositories.UserRepo;
import upuphere.com.upuphere.ui.onboarding.AgreementFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        prefManager = new PrefManager(getActivity());
        setHasOptionsMenu(true);
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        view.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        view.findViewById(R.id.feedbackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("agreementType", AgreementFragment.FEEDBACK_FORM);
                navController.navigate(R.id.agreementFragment, bundle);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nameCardOption:
                Log.d("PROFILE", "NAME CARD");
                break;
            case R.id.moreSettingOption:
                Log.d("PROFILE", "MORE SETTING");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    PrefManager prefManager;
    NavController navController;

    private void logoutUser() {

        prefManager = new PrefManager(getActivity());

        String accessToken = prefManager.getUserAccessToken();
        String refreshToken = prefManager.getUserRefreshToken();

        if(accessToken == null && refreshToken == null){
            prefManager.setIsLoggedIn(false);
            navController.navigate(R.id.loginFragment);
        }
        else if(!DecodeToken.jwtTokenStillValid(accessToken) && !DecodeToken.jwtTokenStillValid(refreshToken)){
            //go to login
            navController.navigate(R.id.loginFragment);
        }else if(!DecodeToken.jwtTokenStillValid(accessToken) && DecodeToken.jwtTokenStillValid(refreshToken)){
            //get the new access token
            UserRepo.getInstance().getRefreshAccessToken(new StringCallBack() {
                @Override
                public void success(String item) {
                    revokedAccess();
                }

                @Override
                public void showError(String error) {

                }
            });
        }else{
            revokedAccess();
        }

    }

    private void revokedAccess(){
        UserRepo.getInstance().revokedAccessToken(new CommonCallBack() {
            @Override
            public void success() {
                Log.d("Revoked access","Successful");
                UserRepo.getInstance().revokeRefreshToken(new CommonCallBack() {
                    @Override
                    public void success() {
                        Log.d("Revoked refresh","Successful");

                        prefManager.setIsLoggedIn(false);
                        navController.navigate(R.id.loginFragment);
                    }

                    @Override
                    public void showError(String error) {

                    }
                });
            }
            @Override
            public void showError(String error) {

            }
        });
    }

}
