package upuphere.com.upuphere.viewmodel;

import android.view.View;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.repositories.RoomRepo;

public class MainViewModel extends ViewModel {

    public enum MainFragmentState{
        MOVE_TO_CREATE_A_ROOM,
    }

    public MutableLiveData<MainFragmentState> mainFragmentState =  new MutableLiveData<>();

    public void onFabClick(View view){
        mainFragmentState.setValue(MainFragmentState.MOVE_TO_CREATE_A_ROOM);
    }

    public LiveData<List<AllRooms>> getRoomList(){
        return RoomRepo.getInstance().getRoomListMutableData();
    }

}
