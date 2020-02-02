package upuphere.com.upuphere.viewmodel;

import android.view.View;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.repositories.RoomRepo;

public class MainViewModel extends ViewModel {

    public interface MainFragmentInterface{
        void onFabClick();
    }
    private MainFragmentInterface mainFragmentInterface;

    public void setMainFragmentInterface(MainFragmentInterface mInterface){
        this.mainFragmentInterface = mInterface;
    }

    public void onFabClick(View view){
        mainFragmentInterface.onFabClick();
    }

    public LiveData<List<AllRooms>> getRoomList(){
        return RoomRepo.getInstance().getRoomListMutableData();
    }

}
