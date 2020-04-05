package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.repositories.UserRepo;

public class PhoneAuthViewModel3 extends AndroidViewModel {

    public String phoneNumber;

    public PhoneAuthViewModel3(@NonNull Application application) {
        super(application);
    }

    UserRepo userRepo = UserRepo.getInstance();

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<String> status = new MutableLiveData<>("");
    public MutableLiveData<String> verifyStatus = new MutableLiveData<>("");
    public MutableLiveData<Boolean> phoneNumberExisted = new MutableLiveData<>(false);

    private void setIsLoading(boolean isLoadingOrNot){
        isLoading.setValue(isLoadingOrNot);
    }

    public void setStatus(String realTimeStatus) {
        status.setValue(realTimeStatus);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public PhoneAuthInterface phoneAuthInterface;

    public interface PhoneAuthInterface{
        void onVerifyButtonClicked();
    }

    public void setPhoneAuthInterface(PhoneAuthInterface phoneAuthInterface) {
        this.phoneAuthInterface = phoneAuthInterface;
    }

    public void onVerifyButtonClicked(View view){
        phoneAuthInterface.onVerifyButtonClicked();
    }

    public void afterPhoneNumberChanged(Editable e){
        status.setValue("");
    }

     public void checkPhoneNumberExisted(String phoneNumber){
        userRepo.checkDetailsExisted(phoneNumber, 333, new BoolCallBack() {
            @Override
            public void success(boolean existed) {
                if(existed){
                    phoneNumberExisted.setValue(true);
                }else{
                    phoneNumberExisted.setValue(false);
                }
            }

            @Override
            public void showError(String error) {
                setStatus(error);
            }
        });
    }


}
