package upuphere.com.upuphere.viewmodel;

import android.view.View;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.repositories.RoomRepo;

public class DisplayRoomViewModel extends ViewModel {

    public interface DisplayRoomInterface{
        void onFabClick();
    }
    private DisplayRoomInterface displayRoomInterface;

    public void setDisplayRoomInterface(DisplayRoomInterface mInterface){
        this.displayRoomInterface = mInterface;
    }


    public void onFabClick(View view){
        //displayRoomFragmentState.setValue(DisplayRoomFragmentState.MOVE_TO_CREATE_POST);
        displayRoomInterface.onFabClick();
    }

    public LiveData<List<Post>> getAllPostInRoom(String roomId){
        return RoomRepo.getInstance().getAllPostWithRoomId(roomId);
    }

}
