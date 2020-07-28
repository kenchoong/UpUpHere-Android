package upuphere.com.upuphere.ui.user;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bigbangbutton.editcodeview.EditCodeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentPhoneAuth3Binding;
import upuphere.com.upuphere.viewmodel.PhoneAuthViewModel3;

import static com.firebase.ui.auth.AuthUI.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneAuthFragment3 extends Fragment implements PhoneAuthViewModel3.PhoneAuthInterface {

    public PhoneAuthFragment3() {
        // Required empty public constructor
    }

    private static final String TAG = "PhoneAuthActivity";

    public static final int FROM_LOGIN_FRAGMENT = 111;
    public static final int FROM_FORGOT_PASSWORD = 222;

    FragmentPhoneAuth3Binding binding;
    PhoneAuthViewModel3 viewModel;
    View rootView;

    private FirebaseAuth mAuth;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_phone_auth3, container, false);

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_phone_auth3,container,false);
        viewModel = new ViewModelProvider(requireActivity()).get(PhoneAuthViewModel3.class);
        rootView = binding.getRoot();

        binding.setViewModel(viewModel);

        mAuth = FirebaseAuth.getInstance();

        createTimer();

        return rootView;
    }

    int previousFragmentCode;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            previousFragmentCode = PhoneAuthFragment3Args.fromBundle(getArguments()).getPreviousFragmentCode();
        }

        viewModel.setPhoneAuthInterface(this);

        binding.ccp.registerPhoneNumberTextView(binding.phoneNumberEdt);

        binding.phoneNumberEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do your stuff here
                    binding.continueButton.performClick();
                    return true;
                }
                return false;
            }
        });

        setInitialState();

        observeInitialStatus();
        observeVerifyStatus();

        binding.editCodeView.setEditCodeListener(new EditCodeListener() {
            @Override
            public void onCodeReady(final String code) {
                binding.verifyButton.setAlpha(1f);
                binding.verifyButton.setEnabled(true);
                binding.verifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        verifyPhoneNumberWithCode(mVerificationId,code);
                    }
                });
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    viewModel.status.setValue("Invalid request!");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    viewModel.status.setValue("Unable to request verification code now");
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId , @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                mVerificationId = verificationId;
                mResendToken = forceResendingToken;

                startVerificationProcess();
            }
        };
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

    private void processingState(){
        disableViews(binding.phoneNumberEdt,binding.continueButton);
        binding.ccp.setClickable(false);
        binding.continueButton.setAlpha(0.5f);
    }

    private void setInitialState(){
        binding.initialState.setVisibility(View.VISIBLE);
        binding.verifyState.setVisibility(View.GONE);
        enableViews(binding.phoneNumberEdt,binding.continueButton);
        binding.ccp.setClickable(true);
        binding.continueButton.setAlpha(1);
    }

    private void startVerificationProcess(){
        binding.initialState.setVisibility(View.GONE);
        binding.verifyState.setVisibility(View.VISIBLE);
        binding.phoneNumberTextView.setText(userPhoneNumber);
        binding.editCodeView.clearCode();
        startTimer();
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            String verifiedPhoneNumber = user.getPhoneNumber();

                            proceedToNextStep(verifiedPhoneNumber,previousFragmentCode);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                viewModel.verifyStatus.setValue("Invalid code!");
                            }
                        }
                    }
                });
    }

    private void proceedToNextStep(String phoneNumberString,int previousFragmentCode) {
        stopTimer();
        Navigation.findNavController(rootView).navigateUp();
/*
        Bundle args = new Bundle();
        args.putString("phone_number", phoneNumberString);
        NavOptions options = new
                NavOptions.Builder().setPopUpTo(R.id.phoneAuthFragment3, true).build();

        switch (previousFragmentCode){
            case FROM_LOGIN_FRAGMENT:
                Navigation.findNavController(rootView).navigate(R.id.signUpFragment, args,options);
                break;
            case FROM_FORGOT_PASSWORD:
                Navigation.findNavController(rootView).navigate(R.id.resetPasswordFragment,args,options);
                break;
        }

 */
    }

    private void sendVerificationCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                userPhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),             // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void observePhoneNumberExisted() {
        viewModel.phoneNumberExisted.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isExisted) {
                sendVerificationCode();
                /*
                switch (previousFragmentCode){
                    case FROM_LOGIN_FRAGMENT:
                        if(isExisted){
                            viewModel.setStatus(getResources().getString(R.string.phone_number_registered));
                            binding.continueButton.setText(R.string.login_instead);
                            binding.continueButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Navigation.findNavController(rootView).navigateUp();
                                }
                            });
                        }else{
                            //todo :: here start verification process
                            //viewModel.status.setValue("Not existed");
                            sendVerificationCode();
                        }
                        break;
                    case FROM_FORGOT_PASSWORD:
                        if(isExisted){
                            //todo :: start verification process
                            //viewModel.status.setValue("Existed");
                            sendVerificationCode();
                        }else{
                            viewModel.setStatus(getResources().getString(R.string.phone_number_not_registered));
                            binding.continueButton.setText(R.string.try_again);
                            setInitialState();
                        }
                        break;
                }

                 */
            }
        });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private CountDownTimer countDownTimer;
    private void createTimer() {
        countDownTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                long remainedSecs = millisUntilFinished / 1000;
                binding.resendButton.setText("Resend at " + (remainedSecs % 60) + " seconds");
            }
            public void onFinish() {
                binding.resendButton.setText(getResources().getString(R.string.resend));
                binding.resendButton.setEnabled(true);
                binding.resendButton.setTextColor(getResources().getColor(R.color.redirect_color));
                binding.resendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendVerificationCode(userPhoneNumber,mResendToken);
                    }
                });
            }
        };
    }

    private void startTimer(){
        countDownTimer.start();
    }

    private void stopTimer(){
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void observeInitialStatus() {
        viewModel.status.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String status) {
                binding.statusText.setText(status);
            }
        });

    }

    private void observeVerifyStatus() {
        viewModel.verifyStatus.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.verificationStatus.setText(s);
            }
        });
    }

    String userPhoneNumber;
    @Override
    public void onVerifyButtonClicked() {
        processingState();

        userPhoneNumber = binding.ccp.getFullNumberWithPlus();
        Log.d("Phone number 12345",userPhoneNumber);

        if(!binding.ccp.isValid()){
            viewModel.status.setValue("Invalid phone number");
            setInitialState();
        }else{

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    //set icon
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    //set title
                    .setTitle("Confirm your phone number")
                    //set message
                    .setMessage(userPhoneNumber + " is your correct phone number?")
                    //set positive button
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what would happen when positive button is clicked
                            viewModel.checkPhoneNumberExisted(userPhoneNumber);
                            observePhoneNumberExisted();
                        }
                    })
                        //set negative button
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what should happen when negative button is clicked
                            setInitialState();
                        }
                    }).show();

        }
    }


}
