package upuphere.com.upuphere.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateRoomViewModel extends ViewModel {

    public enum CreateRoomState{
        EXIT_CREATE_ROOM_PROCESS
    }

    public MutableLiveData<CreateRoomState> createRoomState =  new MutableLiveData<>();


    public void exitCreateRoom(){
        createRoomState.setValue(CreateRoomState.EXIT_CREATE_ROOM_PROCESS);
    }
}
