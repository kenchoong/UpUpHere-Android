package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import upuphere.com.upuphere.Interface.AuthListener;
import upuphere.com.upuphere.repositories.UserRepo;

public class LoginViewModel extends AndroidViewModel{

    public String identityString;
    public String passwordString;

    UserRepo userRepo = UserRepo.getInstance();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public enum AuthenticateState{
        START_AUTHENTICATION,
        UNAUTHENTICATED,
        AUTHENTICATED,
        INVALID_AUTHENTICATED,
        MOVE_TO_REGISTER,
        BACK_TO_LOGIN,
        FORGOT_PASSWORD
    }

    public MutableLiveData<AuthenticateState> authenticateState =  new MutableLiveData<>();

    public void refuseAuthentication(){
        authenticateState.setValue(AuthenticateState.UNAUTHENTICATED);
    }

    public void backToLogin(){
        authenticateState.setValue(AuthenticateState.BACK_TO_LOGIN);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<String> status = new MutableLiveData<>("");

    private void setIsLoading(boolean isLoadingOrNot){
        isLoading.setValue(isLoadingOrNot);
    }

    public void onLoginButtonClick(View view){
        setIsLoading(true);

        if(TextUtils.isEmpty(identityString)){
            setIsLoading(false);
            status.setValue("Please enter your email or username");
        }

        else if(TextUtils.isEmpty(passwordString)){
            setIsLoading(false);
            status.setValue("Please enter your password");
        }

        else if(!TextUtils.isEmpty(identityString) && !TextUtils.isEmpty(passwordString)){
            Log.d("CLICK","Login");
            authenticateState.setValue(AuthenticateState.START_AUTHENTICATION);
            userRepo.loginUser(identityString, passwordString, new AuthListener() {
                @Override
                public void onSuccess() {
                    Log.d("Login","Success");
                    setIsLoading(false);
                    authenticateState.setValue(AuthenticateState.AUTHENTICATED);
                }

                @Override
                public void onFailure(String error) {
                    setIsLoading(false);
                    status.setValue("Invalid email,username or password.Please try again");
                    authenticateState.setValue(AuthenticateState.UNAUTHENTICATED);
                }
            });
        }
    }

    public void onRedirectTextClick(View view){
        authenticateState.setValue(AuthenticateState.MOVE_TO_REGISTER);
    }

    public void onForgotPassword(View view){
        authenticateState.setValue(AuthenticateState.FORGOT_PASSWORD);
    }





}
