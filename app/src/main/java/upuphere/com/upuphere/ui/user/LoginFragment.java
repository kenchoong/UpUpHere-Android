package upuphere.com.upuphere.ui.user;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.helper.KeyboardHelper;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
import upuphere.com.upuphere.databinding.FragmentLoginBinding;
import upuphere.com.upuphere.helper.PrefManager;

import upuphere.com.upuphere.viewmodel.LoginViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements LoginViewModel.LoginInterface {


    public LoginFragment() {
        // Required empty public constructor
    }

    PrefManager prefManager;

    LoginViewModel viewModel;


    FragmentLoginBinding binding;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //final View view = inflater.inflate(R.layout.fragment_login, container, false);
        prefManager = new PrefManager(getActivity());
        
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false);
        viewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        rootView = binding.getRoot();

        binding.setLoginViewModel(viewModel);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final PrefManager prefManager = new PrefManager(getActivity());
        if (prefManager.isLoggedIn()){
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack(R.id.mainFragment,true);
            navController.navigate(R.id.mainFragment);
        }

        viewModel.setLoginInterface(this);

        initializeProgressBar();
        observeLoginStatus();


        final NavController navController = Navigation.findNavController(view);
/*
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        //viewModel.refuseAuthentication();
                        navController.popBackStack(R.id.mainFragment, false);
                    }
                });*/


    }

    private void observeLoginStatus() {
        viewModel.status.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if(!TextUtils.isEmpty(status)){
                    binding.statusText.setVisibility(View.VISIBLE);
                    binding.statusText.setText(status);
                }else{
                    binding.statusText.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initializeProgressBar() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    if(binding.statusText.getVisibility() == View.VISIBLE){
                        binding.statusText.setVisibility(View.GONE);
                    }

                    binding.progressBar.setVisibility(View.VISIBLE);
                }
                else{
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
    }

    @Override
    public void onLoginSuccess() {
        SharedPreferences sharedPreferences = new PrefManager(getActivity()).getPref();
        SharedPreferenceBooleanLiveData sharedPreferenceBooleanLiveData = new SharedPreferenceBooleanLiveData(sharedPreferences,PrefManager.IS_LOGGED_IN,false);

        sharedPreferenceBooleanLiveData.getBooleanLiveData(PrefManager.IS_LOGGED_IN,false).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoggedIn) {
                Log.d("LOGIN 1",String.valueOf(isLoggedIn));
                if(isLoggedIn){
                    updateFirebaseToken();
                }
            }
        });
    }

    private void updateFirebaseToken() {
        viewModel.updateFirebaseToken(prefManager.getFirebaseToken(), new StringCallBack() {
            @Override
            public void success(String item) {
                Log.d("Update firebase token",item);
                NavController navController = Navigation.findNavController(rootView);
                navController.popBackStack(R.id.loginFragment,true);
                navController.navigate(R.id.mainFragment);
            }

            @Override
            public void showError(String error) {
                Log.d("Update firebase token",error);
            }
        });
    }

    @Override
    public void onLoginFailed() {
        Log.d("Error","Some Error");
    }

    @Override
    public void onForgotPasswordClick() {
        NavDirections action1 = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment();
        Navigation.findNavController(rootView).navigate(action1);
    }

    @Override
    public void onRegisterClick() {
        //NavDirections directions = LoginFragmentDirections.actionLoginFragmentToPhoneAuthFragment2(PhoneAuthFragment2.FROM_LOGIN_FRAGMENT);
        NavDirections directions = LoginFragmentDirections.actionLoginFragmentToPhoneAuthFragment3(PhoneAuthFragment3.FROM_LOGIN_FRAGMENT);
        Navigation.findNavController(rootView).navigate(directions);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideKeyBoard();
        binding.identityField.setText("");
        binding.passwordField.setText("");
    }

    private void hideKeyBoard(){
        KeyboardHelper.hideKeyboard(getActivity());
    }
}
