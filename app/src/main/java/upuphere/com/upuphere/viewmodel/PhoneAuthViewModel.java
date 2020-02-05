package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.repositories.UserRepo;
import upuphere.com.upuphere.ui.user.PhoneAuthFragment;

public class PhoneAuthViewModel extends AndroidViewModel {
    private static final String TAG = "PhoneAuthActivity";

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    public PhoneAuthViewModel(@NonNull Application application) {
        super(application);
    }

    UserRepo userRepo = UserRepo.getInstance();

    public enum PhoneAuthenticationState{
       PHONE_NUMBER_REGISTERED,
       INVALID_PHONE_NUMBER,
        PHONE_NUMBER_OK,
        VERIFICATION_FIELD_EMPTY,
       STATE_INITIALIZED,
        STATE_START_VERIFY,
       STATE_CODE_SENT,
       STATE_VERIFY_SUCCESS,
       STATE_VERIFY_FAILED,
        INVALID_CODE,
        TOO_MANY_REQUEST
   }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {

                phoneAuthState.setValue(PhoneAuthenticationState.INVALID_PHONE_NUMBER);
                // [END_EXCLUDE]
            } else if (e instanceof FirebaseTooManyRequestsException) {
                phoneAuthState.setValue(PhoneAuthenticationState.TOO_MANY_REQUEST);
            }
            phoneAuthState.setValue(PhoneAuthenticationState.STATE_VERIFY_FAILED);
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId,token);
            Log.d(TAG, "onCodeSent:" + verificationId);

            phoneAuthState.setValue(PhoneAuthenticationState.STATE_CODE_SENT);

            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    public MutableLiveData<PhoneAuthenticationState> phoneAuthState =  new MutableLiveData<>();

    public String phoneNumber;
    public String verificationField;
    public String detailTextView;

    public void onSendOTPButtonClick(View view) {
        if (!validatePhoneNumber()) {
            Log.d("Phone Number", "Invalid");
            phoneAuthState.setValue(PhoneAuthenticationState.INVALID_PHONE_NUMBER);
        }
        checkDetailsExisted();
    }

    public void onVerifyButtonClick(View view){
        if (TextUtils.isEmpty(verificationField)) {
            phoneAuthState.setValue(PhoneAuthenticationState.VERIFICATION_FIELD_EMPTY);

        }else{
            verifyPhoneNumberWithCode(mVerificationId, verificationField);
        }
    }

    public void onResendButtonClick(View view){
        resendVerificationCode(phoneNumber, mResendToken);
    }

    private void checkDetailsExisted(){
        userRepo.checkDetailsExisted(phoneNumber, 333, new BoolCallBack() {
            @Override
            public void success(boolean existed) {
                if(existed){
                    phoneAuthState.setValue(PhoneAuthenticationState.PHONE_NUMBER_REGISTERED);
                }else{
                    phoneAuthState.setValue(PhoneAuthenticationState.PHONE_NUMBER_OK);
                }
            }

            @Override
            public void showError(String error) {

            }
        });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    public void startPhoneNumberVerification(String phoneNumber) {
        phoneAuthState.setValue(PhoneAuthenticationState.STATE_START_VERIFY);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(TaskExecutors.MAIN_THREAD, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            //updateUI(STATE_VERIFY_SUCCESS);
                            phoneAuthState.setValue(PhoneAuthenticationState.STATE_VERIFY_SUCCESS);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                phoneAuthState.setValue(PhoneAuthenticationState.INVALID_CODE);
                            }

                            //updateUI(STATE_VERIFY_FAILED);
                            phoneAuthState.setValue(PhoneAuthenticationState.STATE_VERIFY_FAILED);
                        }
                    }
                });
    }


    private boolean validatePhoneNumber() {
        return !TextUtils.isEmpty(phoneNumber);
    }
}
