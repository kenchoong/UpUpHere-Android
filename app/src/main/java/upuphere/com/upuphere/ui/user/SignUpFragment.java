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


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentSignUpBinding;

import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
import upuphere.com.upuphere.repositories.UserRepo;
import upuphere.com.upuphere.viewmodel.LoginViewModel;
import upuphere.com.upuphere.viewmodel.SignUpViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private String phoneNumber = null;


    public SignUpFragment() {
        // Required empty public constructor
    }

    private SignUpViewModel signUpViewModel;
    LoginViewModel loginViewModel;
    private FragmentSignUpBinding binding;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        signUpViewModel = ViewModelProviders.of(requireActivity()).get(SignUpViewModel.class);
        loginViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);

        rootView = binding.getRoot();
        binding.setViewModel(signUpViewModel);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signUpViewModel.setSignUpInterface(new SignUpViewModel.SignUpInterface() {
            @Override
            public void onBackToLogin() {
                loginViewModel.backToLogin();
                Navigation.findNavController(rootView).navigate(R.id.loginFragment);
            }
        });

        initProgressBar();

        observeSignUpStatus();

        phoneNumber = Objects.requireNonNull(getArguments()).getString("phone_number");
        Log.d("PHONE_NUMBER", Objects.requireNonNull(phoneNumber));

        final PrefManager prefManager = new PrefManager(getActivity());

        signUpViewModel.signUpState.observe(getViewLifecycleOwner(), new Observer<SignUpViewModel.SignUpState>() {
            @Override
            public void onChanged(SignUpViewModel.SignUpState signUpState) {
                switch (signUpState) {
                    case SIGN_UP_SUCCESS:
                        SharedPreferences sharedPreferences = new PrefManager(getActivity()).getPref();
                        SharedPreferenceBooleanLiveData sharedPreferenceBooleanLiveData = new SharedPreferenceBooleanLiveData(sharedPreferences,PrefManager.IS_LOGGED_IN,false);


                        sharedPreferenceBooleanLiveData.getBooleanLiveData(PrefManager.IS_LOGGED_IN,false).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean isLoggedIn) {
                                if(isLoggedIn){
                                    NavController navController = Navigation.findNavController(view);
                                    navController.popBackStack(R.id.signUpFragment,true);
                                    navController.navigate(R.id.mainFragment);
                                }
                            }
                        });
                        break;
                    case SIGN_UP_ERROR:
                        Log.d("sign up error", "some error");
                        break;
                    case START_CREATE_ACCOUNT:
                        String firebaseToken = prefManager.getFirebaseToken();
                        signUpViewModel.createUserAccount(phoneNumber, signUpViewModel.email, signUpViewModel.username, signUpViewModel.password, firebaseToken);
                        if(TextUtils.isEmpty(firebaseToken)){
                            UserRepo.getInstance().requestTokenFromFirebase(getActivity(), new StringCallBack() {
                                @Override
                                public void success(String newToken) {
                                    signUpViewModel.createUserAccount(phoneNumber, signUpViewModel.email, signUpViewModel.username, signUpViewModel.password, newToken);
                                }

                                @Override
                                public void showError(String error) {
                                    Log.d("Firebase error",error);
                                }
                            });
                        }
                        break;
                    case PASSWORD_DOESNT_MATCH:
                        binding.reenterPasswordField.setError("Password not match");
                        break;
                    case PASSWORD_TOO_SHORT:
                        binding.passwordField.setError("Password must at least 6 character");
                        break;
                    case USERNAME_TOO_SHORT:
                        binding.usernameField.setError("Username must at least 6 character");
                        break;
                    case USERNAME_INVALID:
                        binding.usernameField.setError("Invalid username");
                        break;
                    case USERNAME_TAKEN:
                        binding.usernameField.setError("Username is taken");
                        break;
                    case EMAIL_TAKEN:
                        binding.emailField.setError("Email is taken");
                        break;
                    case EMAIL_INVALID:
                        binding.emailField.setError("Invalid email");
                        break;
                }
            }
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                signUpViewModel.signUpInterface.onBackToLogin();
            }
        });

    }

    private void initProgressBar() {
        signUpViewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    if(binding.signUpStatus.getVisibility() == View.VISIBLE){
                        binding.signUpStatus.setVisibility(View.GONE);
                    }

                    binding.progressBar.setVisibility(View.VISIBLE);
                }
                else{
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void observeSignUpStatus() {
        signUpViewModel.status.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if(!TextUtils.isEmpty(status)){
                    binding.signUpStatus.setVisibility(View.VISIBLE);
                    binding.signUpStatus.setText(status);
                }else{
                    binding.signUpStatus.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
    }
}



