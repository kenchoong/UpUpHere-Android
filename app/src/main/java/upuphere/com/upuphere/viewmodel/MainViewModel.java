package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.view.View;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.repositories.RoomRepo;

public class MainViewModel extends AndroidViewModel {
    private RoomRepo roomRepo;

    public MainViewModel(@NonNull Application application) {
        super(application);
        roomRepo = new RoomRepo(application);
    }

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
        return roomRepo.getRoomListMutableData();
    }

    public void blockUserOrHideRoom(String roomIdOrUserId, int operationType, final StringCallBack callBack){
        setIsLoading(true);

        roomRepo.blockUserOrHideRoom(roomIdOrUserId, operationType, new StringCallBack() {
            @Override
            public void success(String item) {
                setIsLoading(false);
                callBack.success(item);
            }

            @Override
            public void showError(String error) {
                setIsLoading(false);
                callBack.showError(error);
            }
        });
    }

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private void setIsLoading(boolean isLoadingOrNot){
        isLoading.setValue(isLoadingOrNot);
    }

}
