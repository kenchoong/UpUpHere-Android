package upuphere.com.upuphere.viewmodel;

import android.text.Editable;
import android.view.View;

import java.util.regex.Pattern;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.repositories.UserRepo;

public class SignUpViewModel extends ViewModel {

    UserRepo userRepo = UserRepo.getInstance();

    private static final int EMAIL_FIELD = 1;
    private static final int USERNAME_FIELD = 2;
    private static final int PASSWORD_FIELD = 3;
    private static final int REENTER_PASSWORD_FIELD = 4;

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<String> status = new MutableLiveData<>("");

    private void setIsLoading(boolean isLoadingOrNot){
        isLoading.setValue(isLoadingOrNot);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public String email,username,password,confirmPassword;

    public void setSignUpInterface(SignUpInterface signUpInterface) {
        this.signUpInterface = signUpInterface;
    }

    public SignUpInterface signUpInterface;

    public interface SignUpInterface{
        void onBackToLogin();
        void onStartSignUp();
        void onSignUpSuccess();
        void onSignUpError();
        void onEmailValid(Boolean validOrNot);
        void onUsernameTooShort(Boolean tooShortOrNot);
        void onPasswordTooShort(Boolean tooShortOrNot);
        void onPasswordMatch(Boolean matchOrNot);
        void onUsernameValid(Boolean validOrNot);
        void onUsernameTaken(Boolean takenOrNot);
        void onEmailTaken(Boolean takenOrNot);
        void onSkipSignUp();
    }


    public void OnClickRedirectLoginText(View view){
        signUpInterface.onBackToLogin();
    }

    public void OnClickCreateAccountButton(View view){
        signUpInterface.onStartSignUp();
    }

    public void onSkipButtonClicked(View view){
        signUpInterface.onSkipSignUp();
    }

    public void afterUsernameChanged(Editable e){
        checkIfValidCredential(USERNAME_FIELD,username);
    }

    public void afterEmailChanged(Editable e){
        checkIfValidCredential(EMAIL_FIELD,email);
    }

    public void  afterPasswordChanged(Editable e){
        checkIfValidCredential(PASSWORD_FIELD,password);
    }

    public void afterConfirmPasswordChanged(Editable e){
        checkIfValidCredential(REENTER_PASSWORD_FIELD,confirmPassword);
    }

    private void checkIfValidCredential(int fieldTypes,String credential){
        switch (fieldTypes){
            case EMAIL_FIELD:
                if (!isEmailValid(credential)){
                    signUpInterface.onEmailValid(false);
                }else{
                    signUpInterface.onEmailValid(true);
                    checkIfCredentialExisted(credential,EMAIL_FIELD);
                }
                break;
            case USERNAME_FIELD:

                Pattern pattern = Pattern.compile("^(?=.{6,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
                //username is 8-20 characters long
                //no _ or . at the beginning
                //no __ or _. or ._ or .. inside
                //no _ or . at the end
                //allowed a-z ,A-Z, 0-9, . and _

                boolean valid =  pattern.matcher(credential).matches();

                if(!valid){
                    if(credential.length() < 6){
                        signUpInterface.onUsernameTooShort(true);
                    }else {
                        signUpInterface.onUsernameTooShort(false);
                        signUpInterface.onUsernameValid(false);
                    }
                }else {

                    signUpInterface.onUsernameValid(true);
                    checkIfCredentialExisted(credential,USERNAME_FIELD);
                }
                break;
            case PASSWORD_FIELD:
                if(credential.length()< 6){
                    signUpInterface.onPasswordTooShort(true);
                }else {
                    signUpInterface.onPasswordTooShort(false);
                }
                break;

            case REENTER_PASSWORD_FIELD:
                if(!credential.equals(password)){
                    signUpInterface.onPasswordMatch(false);
                }else{
                    signUpInterface.onPasswordMatch(true);
                }
                break;
        }
    }


    private void checkIfCredentialExisted(final String credential, final int identityType){
        //call to server to check
        userRepo.checkDetailsExisted(credential, 222,new BoolCallBack() {
            @Override
            public void success(boolean existed) {
                switch (identityType){
                    case EMAIL_FIELD:
                        if(existed){
                            signUpInterface.onEmailTaken(true);
                        }else{
                            signUpInterface.onEmailTaken(false);
                        }

                        break;
                    case USERNAME_FIELD:
                        if(existed){
                            signUpInterface.onUsernameTaken(true);
                        }else{
                            signUpInterface.onUsernameTaken(false);                        }
                        break;
                }


            }

            @Override
            public void showError(String error) {

            }
        });
    }

    public void createUserAccount(String phoneNumber, String emailString, String username, String passwordString, String firebaseToken) {
        setIsLoading(true);
        //send to server
        userRepo.signUp(phoneNumber, emailString, username, passwordString,firebaseToken, new CommonCallBack() {
            @Override
            public void success() {
                signUpInterface.onSignUpSuccess();
                setIsLoading(false);
            }

            @Override
            public void showError(String error) {
                signUpInterface.onSignUpError();
                setIsLoading(false);
                status.setValue("Error when creating account,please try again later");
            }
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}
