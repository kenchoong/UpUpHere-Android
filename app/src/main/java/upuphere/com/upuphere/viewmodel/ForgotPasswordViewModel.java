package upuphere.com.upuphere.viewmodel;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.repositories.UserRepo;

import static upuphere.com.upuphere.fragment.PhotoBottomSheetDialogFragment.TAG;

public class ForgotPasswordViewModel extends ViewModel {

    private ForgotPassInterface forgotPassInterface;

    public interface ForgotPassInterface {
       void onEmailInserted();
       void onEmailEmpty();
       void onEmailNotRegistered();
       void onEmailSent();
       void onError();
       void onInvalidEmail();
       void onUsingPhoneInstead();
    }

    public void setForgotPassInterface(ForgotPassInterface forgotPassInterface) {
        this.forgotPassInterface = forgotPassInterface;
    }

    public String emailAddress;

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<String> status = new MutableLiveData<>("");

    private void setIsLoading(boolean isLoadingOrNot){
        isLoading.setValue(isLoadingOrNot);
    }

    public void onResetPasswordButtonClick(View view){

        if(isEmailValid(emailAddress)) {
            setIsLoading(true);

            UserRepo.getInstance().checkDetailsExisted(emailAddress, 222, new BoolCallBack() {
                @Override
                public void success(boolean existed) {
                    setIsLoading(false);

                    if (existed) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Email sent.");
                                            forgotPassInterface.onEmailSent();
                                        }
                                    }
                                });
                    } else {
                        forgotPassInterface.onEmailNotRegistered();
                    }
                }

                @Override
                public void showError(String error) {
                    setIsLoading(false);
                    forgotPassInterface.onError();
                }
            });
        }else{
            setIsLoading(false);
            forgotPassInterface.onInvalidEmail();
        }
    }

    public void onUsingPhoneToReset(View view){
        forgotPassInterface.onUsingPhoneInstead();
    }

    public void afterEmailChanged(Editable e){
        if (TextUtils.isEmpty(e.toString().trim())) {
            forgotPassInterface.onEmailEmpty();
        }else{
            forgotPassInterface.onEmailInserted();
        }

    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
