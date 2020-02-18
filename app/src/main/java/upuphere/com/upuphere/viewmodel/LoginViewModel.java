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

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<String> status = new MutableLiveData<>("");

    public void setLoginInterface(LoginInterface signUpInterface) {
        this.loginInterface = signUpInterface;
    }

    public LoginInterface loginInterface;

    public interface LoginInterface{
        void onLoginSuccess();
        void onLoginFailed();
        void onForgotPasswordClick();
        void onRegisterClick();
    }

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
            userRepo.loginUser(identityString, passwordString, new AuthListener() {
                @Override
                public void onSuccess() {
                    Log.d("Login","Success");
                    setIsLoading(false);
                    loginInterface.onLoginSuccess();
                }

                @Override
                public void onFailure(String error) {
                    setIsLoading(false);
                    status.setValue("Invalid email,username or password.Please try again");
                    loginInterface.onLoginFailed();
                }
            });
        }
    }

    public void onRedirectTextClick(View view){
        loginInterface.onRegisterClick();
    }

    public void onForgotPassword(View view){
        loginInterface.onForgotPasswordClick();
    }





}
