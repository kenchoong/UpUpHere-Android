package upuphere.com.upuphere.ui.user;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.repositories.UserRepo;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneAuthFragment2 extends Fragment {


    public PhoneAuthFragment2() {
        // Required empty public constructor
    }

    private static final int RC_SIGN_IN = 1822;
    public static final int FROM_LOGIN_FRAGMENT = 111;
    public static final int FROM_FORGOT_PASSWORD = 222;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_phone_auth_fragment2, container, false);
        continueButton = rootView.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<AuthUI.IdpConfig> provider = Collections.singletonList(
                        new AuthUI.IdpConfig.PhoneBuilder().build());

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(provider)
                                .build(),
                        RC_SIGN_IN);
            }
        });

        instructionText = rootView.findViewById(R.id.instructionText);
        statusText = rootView.findViewById(R.id.statusText);

        return rootView;
    }

    View rootView;
    TextView instructionText,statusText;
    Button continueButton;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);



            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                final String phone_number = response.getPhoneNumber();

                Log.d("Phone Auth",phone_number);

                UserRepo.getInstance().checkDetailsExisted(phone_number, 333, new BoolCallBack() {
                    @Override
                    public void success(boolean existed) {
                        switch (previousFragmentCode){
                            case FROM_LOGIN_FRAGMENT:
                                if(existed){
                                    statusText.setText(R.string.phone_number_registered);
                                    continueButton.setText(R.string.login_instead);
                                    continueButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Navigation.findNavController(rootView).navigateUp();
                                        }
                                    });
                                }else{
                                    NavDirections directions = PhoneAuthFragment2Directions.actionPhoneAuthFragment2ToSignUpFragment(phone_number);
                                    Navigation.findNavController(rootView).navigate(directions);
                                }

                                break;
                            case FROM_FORGOT_PASSWORD:
                                if(existed){
                                    NavDirections directions1 = PhoneAuthFragment2Directions.actionPhoneAuthFragment2ToResetPasswordFragment(phone_number);
                                    Navigation.findNavController(rootView).navigate(directions1);
                                }else{
                                    statusText.setText(R.string.phone_number_not_registered);
                                    continueButton.setText(R.string.try_again);
                                }
                                break;
                        }
                    }

                    @Override
                    public void showError(String error) {
                        statusText.setText(R.string.unknown_error);
                    }
                });

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    int previousFragmentCode;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            previousFragmentCode = PhoneAuthFragment2Args.fromBundle(getArguments()).getPreviousFragmentCode();
        }


        switch (previousFragmentCode){
            case FROM_FORGOT_PASSWORD:
                instructionText.setText(R.string.forgot_password_instruction);
                break;
            case FROM_LOGIN_FRAGMENT:
                instructionText.setText(R.string.verify_phone_number_reason);
                break;
        }
    }
}