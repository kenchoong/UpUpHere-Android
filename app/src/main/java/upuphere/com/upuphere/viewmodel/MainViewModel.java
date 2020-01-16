package upuphere.com.upuphere.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public enum MainFragmentState{
        MOVE_TO_CREATE_A_ROOM
    }

    public MutableLiveData<MainFragmentState> mainFragmentState =  new MutableLiveData<>();

    public void onFabClick(View view){
        mainFragmentState.setValue(MainFragmentState.MOVE_TO_CREATE_A_ROOM);
    }


}
