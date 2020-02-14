package upuphere.com.upuphere.ui.user;


import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentPhoneAuthBinding;
import upuphere.com.upuphere.viewmodel.LoginViewModel;
import upuphere.com.upuphere.viewmodel.PhoneAuthViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneAuthFragment extends Fragment{
    public PhoneAuthFragment() {
        // Required empty public constructor
    }

    private PhoneAuthViewModel phoneAuthViewModel;
    private FragmentPhoneAuthBinding phoneAuthBinding;
    private LoginViewModel loginViewModel;
    View rootView;

    public static final int FROM_LOGIN_FRAGMENT = 111;
    public static final int FROM_FORGOT_PASSWORD = 222;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        phoneAuthBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_phone_auth,container,false);
        phoneAuthViewModel = ViewModelProviders.of(requireActivity()).get(PhoneAuthViewModel.class);
        loginViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        rootView = phoneAuthBinding.getRoot();
        phoneAuthBinding.setViewModel(phoneAuthViewModel);
        return rootView;
    }

    int previousFragmentCode;
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            previousFragmentCode = getArguments().getInt("previous_fragment_code");
        }

        phoneAuthViewModel.phoneAuthState.observe(getViewLifecycleOwner(), new Observer<PhoneAuthViewModel.PhoneAuthenticationState>() {
            @Override
            public void onChanged(PhoneAuthViewModel.PhoneAuthenticationState phoneAuthenticationState) {
                switch (phoneAuthenticationState) {
                    case STATE_INITIALIZED:
                        enableViews(phoneAuthBinding.phoneNumberField, phoneAuthBinding.sendOTPbutton);
                        disableViews(phoneAuthBinding.verificationField, phoneAuthBinding.verificationField, phoneAuthBinding.resendButton);
                        break;
                    case INVALID_PHONE_NUMBER:
                        phoneAuthBinding.phoneNumberField.setError("Invalid Phone Number");
                        break;
                    case PHONE_NUMBER_REGISTERED:
                        if(previousFragmentCode == FROM_FORGOT_PASSWORD){
                            phoneAuthViewModel.phoneAuthState.setValue(PhoneAuthViewModel.PhoneAuthenticationState.PHONE_NUMBER_OK);
                        }
                        else if(previousFragmentCode == FROM_LOGIN_FRAGMENT) {
                            phoneAuthBinding.phoneNumberField.setError("Phone Number already registered");
                        }
                        break;

                    case PHONE_NUMBER_OK:
                        phoneAuthViewModel.startPhoneNumberVerification(phoneAuthViewModel.phoneNumber);
                        break;
                    case INVALID_CODE:
                        phoneAuthBinding.detailsTextView.setError("Invalid code");
                        break;
                    case STATE_START_VERIFY:
                        enableViews(phoneAuthBinding.verificationField, phoneAuthBinding.verifyButton, phoneAuthBinding.resendButton);
                        disableViews(phoneAuthBinding.phoneNumberField, phoneAuthBinding.sendOTPbutton);
                        break;
                    case STATE_CODE_SENT:
                        phoneAuthBinding.verificationField.setVisibility(View.VISIBLE);
                        phoneAuthBinding.verifyButton.setVisibility(View.VISIBLE);
                        phoneAuthBinding.resendButton.setVisibility(View.VISIBLE);
                        break;
                    case TOO_MANY_REQUEST:
                        phoneAuthBinding.detailsTextView.setText("Too many request");
                        break;
                    case STATE_VERIFY_SUCCESS:
                        // go to next activity
                        phoneAuthBinding.detailsTextView.setText("Verify success");
                        proceedToNextStep(phoneAuthViewModel.phoneNumber, previousFragmentCode);
                        break;
                    case STATE_VERIFY_FAILED:
                        disableViews(phoneAuthBinding.verifyButton);
                        enableViews(phoneAuthBinding.phoneNumberField, phoneAuthBinding.verificationField, phoneAuthBinding.resendButton);
                        phoneAuthBinding.detailsTextView.setText("Verify Failed");
                        break;
                    case VERIFICATION_FIELD_EMPTY:
                        Toast.makeText(getActivity(), "Verification field empty", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                loginViewModel.backToLogin();
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        phoneAuthViewModel.phoneAuthState.setValue(PhoneAuthViewModel.PhoneAuthenticationState.STATE_INITIALIZED);
    }


    private void proceedToNextStep(String phoneNumberString,int previousFragmentCode) {
        Bundle args = new Bundle();
        args.putString("phone_number", phoneNumberString);
        NavOptions options = new
                NavOptions.Builder().setPopUpTo(R.id.phoneAuthFragment, true).build();

        switch (previousFragmentCode){
            case FROM_LOGIN_FRAGMENT:
                Navigation.findNavController(rootView).navigate(R.id.signUpFragment, args,options);
                break;
            case FROM_FORGOT_PASSWORD:
                Navigation.findNavController(rootView).navigate(R.id.resetPasswordFragment,args,options);
                break;

        }
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
    }
}
