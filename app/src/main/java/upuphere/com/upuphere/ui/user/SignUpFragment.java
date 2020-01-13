package upuphere.com.upuphere.ui.user;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentSignUpBinding;

import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.SharedPreferenceBooleanLiveData;
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
    private FragmentSignUpBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        signUpViewModel = ViewModelProviders.of(requireActivity()).get(SignUpViewModel.class);

        View view = binding.getRoot();
        binding.setViewModel(signUpViewModel);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumber = Objects.requireNonNull(getArguments()).getString("phone_number");
        Log.d("PHONE_NUMBER", Objects.requireNonNull(phoneNumber));

        final PrefManager prefManager = new PrefManager(getActivity());

        signUpViewModel.signUpState.observe(getViewLifecycleOwner(), new Observer<SignUpViewModel.SignUpState>() {
            @Override
            public void onChanged(SignUpViewModel.SignUpState signUpState) {
                switch (signUpState) {
                    case REDIRECT_LOGIN:
                        break;
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
                        signUpViewModel.createUserAccount(phoneNumber, signUpViewModel.email, signUpViewModel.username, signUpViewModel.password);
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



    }
}



