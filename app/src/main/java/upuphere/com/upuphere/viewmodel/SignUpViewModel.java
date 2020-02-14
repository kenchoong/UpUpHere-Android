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

    public enum SignUpState{
        REDIRECT_LOGIN,
        START_CREATE_ACCOUNT,
        SIGN_UP_SUCCESS,
        SIGN_UP_ERROR,
        EMAIL_INVALID,
        USERNAME_TOO_SHORT,
        USERNAME_INVALID,
        PASSWORD_TOO_SHORT,
        PASSWORD_DOESNT_MATCH,
        USERNAME_TAKEN,
        EMAIL_TAKEN
    }

    private static final int EMAIL_FIELD = 1;
    private static final int USERNAME_FIELD = 2;
    private static final int PASSWORD_FIELD = 3;
    private static final int REENTER_PASSWORD_FIELD = 4;

    public MutableLiveData<SignUpState> signUpState = new MutableLiveData<>();

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
    }


    public void OnClickRedirectLoginText(View view){
        signUpInterface.onBackToLogin();
    }

    public void OnClickCreateAccountButton(View view){
        signUpState.setValue(SignUpState.START_CREATE_ACCOUNT);
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
                    signUpState.setValue(SignUpState.EMAIL_INVALID);
                }

                checkIfCredentialExisted(credential,EMAIL_FIELD);
                break;
            case USERNAME_FIELD:
                if(credential.length() < 6){
                    signUpState.setValue(SignUpState.USERNAME_TOO_SHORT);
                }
                Pattern pattern = Pattern.compile("^(?=.{6,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
                //username is 8-20 characters long
                //no _ or . at the beginning
                //no __ or _. or ._ or .. inside
                //no _ or . at the end
                //allowed a-z ,A-Z, 0-9, . and _

                boolean valid =  pattern.matcher(credential).matches();

                if(!valid){
                    signUpState.setValue(SignUpState.USERNAME_INVALID);
                }

                checkIfCredentialExisted(credential,USERNAME_FIELD);

                break;
            case PASSWORD_FIELD:
                if(credential.length()< 6){
                    signUpState.setValue(SignUpState.PASSWORD_TOO_SHORT);
                }
                break;

            case REENTER_PASSWORD_FIELD:
                //todo :: check for password and reenter is the same
                if(credential.length() < 6) {
                    signUpState.setValue(SignUpState.PASSWORD_TOO_SHORT);
                }

                if(!credential.equals(password)){
                    signUpState.setValue(SignUpState.PASSWORD_DOESNT_MATCH);
                }

                break;
        }
    }


    private void checkIfCredentialExisted(final String credential, final int identityType){
        //call to server to check
        userRepo.checkDetailsExisted(credential, 222,new BoolCallBack() {
            @Override
            public void success(boolean existed) {
                if(existed){
                    switch (identityType){
                        case EMAIL_FIELD:
                            signUpState.setValue(SignUpState.EMAIL_TAKEN);
                            break;
                        case USERNAME_FIELD:
                            signUpState.setValue(SignUpState.USERNAME_TAKEN);
                            break;
                    }
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
                signUpState.setValue(SignUpState.SIGN_UP_SUCCESS);
                setIsLoading(false);
            }

            @Override
            public void showError(String error) {
                signUpState.setValue(SignUpState.SIGN_UP_ERROR);
                setIsLoading(false);
                status.setValue("Error when creating account,please try again later");
            }
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}
