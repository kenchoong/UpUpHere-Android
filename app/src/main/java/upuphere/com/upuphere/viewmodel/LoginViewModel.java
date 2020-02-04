package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;

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

    public void onLoginButtonClick(View view){
        Log.d("CLICK","Login");
        authenticateState.setValue(AuthenticateState.START_AUTHENTICATION);
        userRepo.loginUser(identityString, passwordString, new AuthListener() {
            @Override
            public void onSuccess() {
                Log.d("Login","Success");
                authenticateState.setValue(AuthenticateState.AUTHENTICATED);
            }

            @Override
            public void onFailure(String error) {
                authenticateState.setValue(AuthenticateState.UNAUTHENTICATED);
            }
        });
    }

    public void onRedirectTextClick(View view){
        authenticateState.setValue(AuthenticateState.MOVE_TO_REGISTER);
    }

    public void onForgotPassword(View view){
        authenticateState.setValue(AuthenticateState.FORGOT_PASSWORD);
    }





}
