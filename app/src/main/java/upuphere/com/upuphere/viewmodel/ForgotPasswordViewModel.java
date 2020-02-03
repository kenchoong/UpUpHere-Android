package upuphere.com.upuphere.viewmodel;

import android.view.View;

import androidx.lifecycle.ViewModel;

public class ForgotPasswordViewModel extends ViewModel {

    public ForgotPassInterface forgotPassInterface;

    public interface ForgotPassInterface {
       void onResetPasswordButtonClick();
    }

    public void setForgotPassInterface(ForgotPassInterface forgotPassInterface) {
        this.forgotPassInterface = forgotPassInterface;
    }

    public String emailAddress;

    public void onResetPasswordButtonClick(View view){
        forgotPassInterface.onResetPasswordButtonClick();
    }



}
