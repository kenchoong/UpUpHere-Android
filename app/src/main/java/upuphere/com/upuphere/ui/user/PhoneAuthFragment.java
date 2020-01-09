package upuphere.com.upuphere.ui.user;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.libs.Authenticate;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneAuthFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "PhoneAuthActivity";

    EditText phoneNumberField,verificationField;
    Button sendOTPbutton,verifyButton,resendButton;

    TextView detailsTextView;


    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private FirebaseAuth mAuth;


    public PhoneAuthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_auth, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
/*
        Button button = view.findViewById(R.id.upto);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = PhoneAuthFragmentDirections.actionPhoneAuthFragmentToSignUpFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
*/
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumberField = view.findViewById(R.id.phoneNumberField);
        verificationField = view.findViewById(R.id.verificationField);
        sendOTPbutton = view.findViewById(R.id.sendOTPbutton);
        verifyButton = view.findViewById(R.id.verifyButton);
        resendButton = view.findViewById(R.id.resendButton);
        detailsTextView = view.findViewById(R.id.detailsTextView);

        sendOTPbutton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    phoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(view.findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull  String verificationId, @NonNull  PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendOTPbutton:
                if(!validatePhoneNumber()){
                    Log.d("Phone Number","Invalid");
                    phoneNumberField.setError("Invalid phone number");
                    return;
                }
                //startPhoneNumberVerification(phoneNumberField.getText().toString());

                Authenticate.checkDetailsExisted(getActivity(),phoneNumberField.getText().toString(), 333, new BoolCallBack() {
                    @Override
                    public void success(boolean existed) {
                        if(existed){
                            phoneNumberField.setError("Phone number registered");
                        }else{
                            startPhoneNumberVerification(phoneNumberField.getText().toString());
                        }
                    }

                    @Override
                    public void showError(String error) {

                    }
                });

                break;
            case R.id.verifyButton:
                String code = verificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    verificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.resendButton:
                resendVerificationCode(phoneNumberField.getText().toString(), mResendToken);
                break;

        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            updateUI(STATE_VERIFY_SUCCESS);

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                                detailsTextView.setError("Invalid code.");

                            }

                            updateUI(STATE_VERIFY_FAILED);

                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


        mVerificationInProgress = true;
    }

    private void updateUI(int uiState){
        switch (uiState){
            case STATE_INITIALIZED:
                enableViews(phoneNumberField,sendOTPbutton);
                disableViews(verificationField,verificationField,resendButton);
                break;
            case STATE_CODE_SENT:
                enableViews(verificationField,verifyButton,resendButton);
                disableViews(phoneNumberField,sendOTPbutton);

                verificationField.setVisibility(View.VISIBLE);
                verifyButton.setVisibility(View.VISIBLE);
                resendButton.setVisibility(View.VISIBLE);
                break;
            case STATE_VERIFY_SUCCESS:
                // go to next activity
                detailsTextView.setText("Verify success");
                proceedToSignUp(detailsTextView,phoneNumberField.getText().toString());
                break;
            case STATE_VERIFY_FAILED:
                disableViews(verifyButton);
                enableViews(phoneNumberField,verificationField,resendButton);
                detailsTextView.setText("Verify Failed");
                break;

        }
    }

    private void proceedToSignUp(View view,String phoneNumberString) {
        //Intent intent = new Intent(getActivity(), SignUpActivity.class);
        //intent.putExtra("phone_number",phoneNumberString);
        //startActivity(intent);

        NavDirections action = PhoneAuthFragmentDirections.actionPhoneAuthFragmentToSignUpFragment(phoneNumberString);
        Navigation.findNavController(view).navigate(action);

    }


    @Override
    public void onStart() {
        super.onStart();
        updateUI(STATE_INITIALIZED);
    }



    private boolean validatePhoneNumber() {
        String phoneNumber = phoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberField.setError("Invalid phone number.");
            return false;
        }

        return true;
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
}
