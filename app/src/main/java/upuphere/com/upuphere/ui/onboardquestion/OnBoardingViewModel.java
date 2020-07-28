package upuphere.com.upuphere.ui.onboardquestion;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import upuphere.com.upuphere.viewmodel.SignUpViewModel;

public class OnBoardingViewModel extends AndroidViewModel {

    public OnBoardingViewModel(@NonNull Application application) {
        super(application);
    }

    public OnBoardingInterface onBoardingInterface;
    public void setOnBoardingInterface(OnBoardingInterface onBoardingInterface) {
        this.onBoardingInterface = onBoardingInterface;
    }

    public interface OnBoardingInterface{
        void onGenderNext();
        void onDobNext();
        void onPreferencesNext();
    }

    public void onGenderNextButtonClicked(View view){
        onBoardingInterface.onGenderNext();
    }

    public void onDobNextButtonClicked(View view){
        onBoardingInterface.onDobNext();
    }

    public void onPreferencesNextButtonClicked(View view){
        onBoardingInterface.onPreferencesNext();
    }
}
