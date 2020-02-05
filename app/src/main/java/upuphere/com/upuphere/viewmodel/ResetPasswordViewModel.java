package upuphere.com.upuphere.viewmodel;

import android.view.View;

import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.repositories.UserRepo;
import upuphere.com.upuphere.ui.user.ResetPasswordFragment;

public class ResetPasswordViewModel extends ViewModel {
    public static final int NEW_PASSWORD_FIELD_EMPTY = 111;
    public static final int CONFIRM_PASSWORD_FIELD_EMPTY = 222;

    private  onResetPasswordListener onResetPasswordListener;

    public interface onResetPasswordListener{
        void onFieldIsEmpty(int whichField);
        void onFieldNotMatch();
        void onFieldMatch();
        void onPasswordTooShort();
    }

    public void setOnResetPasswordListener(ResetPasswordViewModel.onResetPasswordListener onResetPasswordListener) {
        this.onResetPasswordListener = onResetPasswordListener;
    }


    public String newPassword,confirmNewPassword;

    public void onConfirmButtonClick(View view){
        if(!newPassword.isEmpty() && !confirmNewPassword.isEmpty()){
            if(newPassword.length() > 6){
                onResetPasswordListener.onPasswordTooShort();
            }

            if(newPassword.equals(confirmNewPassword)){
                onResetPasswordListener.onFieldMatch();
            }else{
                onResetPasswordListener.onFieldNotMatch();
            }
        }else {
            int whichField = 0;
            if(newPassword.isEmpty()){
                whichField = NEW_PASSWORD_FIELD_EMPTY;
            }
            if(confirmNewPassword.isEmpty()){
                whichField = CONFIRM_PASSWORD_FIELD_EMPTY;
            }
            onResetPasswordListener.onFieldIsEmpty(whichField);
        }
    }

}
