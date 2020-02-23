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
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.models.RoomModel;
import upuphere.com.upuphere.repositories.PostRepo;
import upuphere.com.upuphere.repositories.RoomRepo;

public class DisplayRoomViewModel extends AndroidViewModel {
    private PostRepo postRepo;

    public DisplayRoomViewModel(@NonNull Application application) {
        super(application);
        postRepo = new PostRepo(application);
    }

    public interface DisplayRoomInterface{
        void onFabClick();
        void onRoomBlocked(String message);
        void onClickUnhideRoomButton();
    }
    private DisplayRoomInterface displayRoomInterface;

    public void setDisplayRoomInterface(DisplayRoomInterface mInterface){
        this.displayRoomInterface = mInterface;
    }


    public void onFabClick(View view){
        //displayRoomFragmentState.setValue(DisplayRoomFragmentState.MOVE_TO_CREATE_POST);
        displayRoomInterface.onFabClick();
    }

    public void onUnhideButtonClick(View view){
        displayRoomInterface.onClickUnhideRoomButton();
    }

    public void unHideRoom(String roomId, final StringCallBack callBack){
        postRepo.unHideRoom(roomId, new StringCallBack() {
            @Override
            public void success(String item) {
                callBack.success(item);
            }

            @Override
            public void showError(String error) {
                callBack.showError(error);
            }
        });
    }

    public void blockUserOrHidePost(String postOrUserId, int operationType, final StringCallBack callback){
        postRepo.blockSomething(postOrUserId,null, operationType, new StringCallBack() {
            @Override
            public void success(String item) {

                callback.success(item);
            }

            @Override
            public void showError(String error) {

                callback.showError(error);
            }
        });
    }

    public LiveData<List<Post>> getAllPostInRoom(String roomId){
        return postRepo.getAllPostWithRoomId(roomId, new StringCallBack() {
            @Override
            public void success(String item) {
                //do nothing
            }

            @Override
            public void showError(String error) {
                displayRoomInterface.onRoomBlocked(error);
            }
        });
    }

    public void setPostListToBlank(){
        postRepo.setPostMutableLiveDataToNull();
    }

}
