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
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentSignUpBinding;

import upuphere.com.upuphere.helper.KeyboardHelper;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
import upuphere.com.upuphere.helper.SpannableStringHelper;
import upuphere.com.upuphere.repositories.UserRepo;
import upuphere.com.upuphere.ui.onboarding.AgreementFragment;
import upuphere.com.upuphere.ui.onboarding.WelcomeFragmentDirections;
import upuphere.com.upuphere.viewmodel.LoginViewModel;
import upuphere.com.upuphere.viewmodel.SignUpViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements SignUpViewModel.SignUpInterface {
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
        signUpViewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        rootView = binding.getRoot();
        binding.setViewModel(signUpViewModel);

        createSpannableTextViewAndButton();
        makeLinks();

        return rootView;
    }

    private void makeLinks() {
        List<String> stringToClick = new ArrayList<>();
        stringToClick.add(getResources().getString(R.string.term_of_use));
        stringToClick.add(getResources().getString(R.string.privacy_policy));

        ClickableSpan goToTerms = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Bundle bundle = new Bundle();
                bundle.putInt("agreementType", AgreementFragment.TERM_OF_USE);
                Navigation.findNavController(rootView).navigate(R.id.agreementFragment, bundle);
            }
        };

        ClickableSpan goToPrivacyPolicy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Bundle bundle = new Bundle();
                bundle.putInt("agreementType", AgreementFragment.PRIVACY_POLICY);
                Navigation.findNavController(rootView).navigate(R.id.agreementFragment, bundle);
            }
        };

        List<ClickableSpan>clickableActions = new ArrayList<>();
        clickableActions.add(goToTerms);
        clickableActions.add(goToPrivacyPolicy);

        SpannableStringHelper.makeLinks(binding.termAndPrivacyPolicy, getResources().getString(R.string.term_and_policy),  stringToClick, clickableActions);
    }

    private void createSpannableTextViewAndButton() {
        SpannableStringHelper.createSpannableTextButton(binding.redirectLoginText, getResources().getString(R.string.redirect_sign_in), "Login", new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(getActivity(),"Login",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signUpViewModel.setSignUpInterface(this);

        initProgressBar();

        observeSignUpStatus();

        phoneNumber = Objects.requireNonNull(getArguments()).getString("phone_number");
        //Log.d("PHONE_NUMBER", Objects.requireNonNull(phoneNumber));

        prefManager = new PrefManager(getActivity());

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                signUpViewModel.signUpInterface.onBackToLogin();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        hideKeyBoard();
    }

    private void hideKeyBoard(){
        KeyboardHelper.hideKeyboard(getActivity());
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

    @Override
    public void onBackToLogin() {
        Navigation.findNavController(rootView).navigate(R.id.loginFragment);
    }

    @Override
    public void onSkipSignUp() {
        prefManager.setIsSkipSignedUp(true);
        NavController navController = Navigation.findNavController(rootView);
        navController.popBackStack(R.id.signUpFragment, true);
        navController.navigate(R.id.mainFragment);
    }

    PrefManager prefManager;
    @Override
    public void onStartSignUp() {
        String firebaseToken = prefManager.getFirebaseToken();
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
        }else{
            signUpViewModel.createUserAccount(phoneNumber,signUpViewModel.email,signUpViewModel.username,signUpViewModel.password,firebaseToken);
        }
    }

    @Override
    public void onSignUpSuccess() {
        SharedPreferences sharedPreferences = new PrefManager(getActivity()).getPref();
        SharedPreferenceBooleanLiveData sharedPreferenceBooleanLiveData = new SharedPreferenceBooleanLiveData(sharedPreferences,PrefManager.IS_LOGGED_IN,false);


        sharedPreferenceBooleanLiveData.getBooleanLiveData(PrefManager.IS_LOGGED_IN,false).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoggedIn) {
                if(isLoggedIn){
                    NavController navController = Navigation.findNavController(rootView);
                    navController.popBackStack(R.id.signUpFragment,true);
                    navController.navigate(R.id.onboardingFragment);
                }
            }
        });
    }

    @Override
    public void onSignUpError() {
        Log.d("sign up error", "some error");
    }

    @Override
    public void onEmailValid(Boolean valid) {
        if(!valid){
            binding.email.setErrorEnabled(true);
            binding.email.setError("Invalid email");
        }else{
            binding.email.setError(null);
            binding.email.setErrorEnabled(false);
        }
    }

    @Override
    public void onUsernameTooShort(Boolean tooShort) {
        if(tooShort){
            binding.username.setErrorEnabled(true);
            binding.username.setError("Username must at least 6 character");
        }else{
            binding.username.setError(null);
            binding.username.setErrorEnabled(false);
        }
    }

    @Override
    public void onPasswordTooShort(Boolean tooShort) {
        if(tooShort){
            binding.password.setErrorEnabled(true);
            binding.password.setError("Password must at least 6 character");
        }else{
            binding.password.setError(null);
            binding.password.setErrorEnabled(false);
        }
    }

    @Override
    public void onPasswordMatch(Boolean match) {
        if(!match){
            binding.confirmPassword.setErrorEnabled(true);
            binding.confirmPassword.setError("Password not match");
        }else{
            binding.confirmPassword.setError(null);
            binding.confirmPassword.setErrorEnabled(false);
        }
    }

    @Override
    public void onUsernameValid(Boolean valid) {
        if(!valid){
            binding.username.setErrorEnabled(true);
            binding.username.setError("Invalid username");
        }else{
            binding.username.setError(null);
            binding.username.setErrorEnabled(false);
        }
    }

    @Override
    public void onUsernameTaken(Boolean taken) {
        if(taken){
            binding.username.setErrorEnabled(true);
            binding.username.setError("Username is taken");
        }else{
            binding.username.setError(null);
            binding.username.setErrorEnabled(false);
        }
    }

    @Override
    public void onEmailTaken(Boolean taken) {
        if(taken){
            binding.email.setErrorEnabled(true);
            binding.email.setError("Email is taken");
        }else {
            binding.email.setError(null);
            binding.email.setErrorEnabled(false);
        }
    }
}



