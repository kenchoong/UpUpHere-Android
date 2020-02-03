package upuphere.com.upuphere.ui.user;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
import upuphere.com.upuphere.databinding.FragmentLoginBinding;
import upuphere.com.upuphere.helper.PrefManager;

import upuphere.com.upuphere.viewmodel.LoginViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }

    PrefManager prefManager;

    LoginViewModel viewModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //final View view = inflater.inflate(R.layout.fragment_login, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();

        prefManager = new PrefManager(getActivity());
        
        FragmentLoginBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false);
        viewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        View view = binding.getRoot();

        binding.setLoginViewModel(viewModel);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final PrefManager prefManager = new PrefManager(getActivity());
        if (prefManager.isLoggedIn()){
            viewModel.authenticateState.setValue(LoginViewModel.AuthenticateState.AUTHENTICATED);
        }


        final NavController navController = Navigation.findNavController(view);
        viewModel.authenticateState.observe(getViewLifecycleOwner(), new Observer<LoginViewModel.AuthenticateState>() {
            @Override
            public void onChanged(LoginViewModel.AuthenticateState authenticateState) {
                switch (authenticateState){
                    case START_AUTHENTICATION:
                        //Log.d("LOGIN","START");
                    case AUTHENTICATED:

                        SharedPreferences sharedPreferences = new PrefManager(getActivity()).getPref();
                        SharedPreferenceBooleanLiveData sharedPreferenceBooleanLiveData = new SharedPreferenceBooleanLiveData(sharedPreferences,PrefManager.IS_LOGGED_IN,false);


                        sharedPreferenceBooleanLiveData.getBooleanLiveData(PrefManager.IS_LOGGED_IN,false).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean isLoggedIn) {
                                if(isLoggedIn){
                                    NavController navController = Navigation.findNavController(view);
                                    navController.popBackStack(R.id.loginFragment,true);
                                    navController.navigate(R.id.mainFragment);
                                }
                            }
                        });
                        break;
                    case UNAUTHENTICATED:
                        Log.d("LOGIN","INVALID");
                        break;
                    case INVALID_AUTHENTICATED:
                        Log.d("LOGIN","UNAUTHENTICATED");
                        break;
                    case MOVE_TO_REGISTER:
                        NavDirections action = LoginFragmentDirections.actionLoginFragmentToRegistrationGraph();
                        navController.navigate(action);
                        break;
                    case BACK_TO_LOGIN:
                        Log.d("LOGIN","BACK TO LOGIN");
                        break;

                    case FORGOT_PASSWORD:
                        NavDirections action1 = LoginFragmentDirections.actionLoginFragmentToForgotPasswordGraph();
                        navController.navigate(action1);
                        break;
                }
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        viewModel.refuseAuthentication();
                        navController.popBackStack(R.id.mainFragment, false);
                    }
                });


    }



}
