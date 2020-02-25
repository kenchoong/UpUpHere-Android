package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.Interface.GetResultListener;
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
        void onRoomOrUserBlocked(String message,int blockType,String blockItemId);
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
        setIsLoading(true);
        postRepo.unHideRoom(roomId, new StringCallBack() {
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

    public void blockUserOrHidePost(String postOrUserId, int operationType, final StringCallBack callback){
        setIsLoading(true);
        postRepo.blockSomething(postOrUserId,null, operationType, new StringCallBack() {
            @Override
            public void success(String item) {
                setIsLoading(false);
                callback.success(item);
            }

            @Override
            public void showError(String error) {
                setIsLoading(false);
                callback.showError(error);
            }
        });
    }

    public LiveData<List<Post>> getAllPostInRoom(final String roomId){
        return postRepo.getAllPostWithRoomId(roomId, new GetResultListener() {
            @Override
            public void onHidedItem(String message, int blockType) {
                displayRoomInterface.onRoomOrUserBlocked(message,blockType,roomId);
            }

            @Override
            public void onBlockedUser(String message, int blockType, String blockUserId) {
                displayRoomInterface.onRoomOrUserBlocked(message,blockType,blockUserId);
            }
        });
    }

    public void setPostListToBlank(){
        postRepo.setPostMutableLiveDataToNull();
    }

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private void setIsLoading(boolean isLoadingOrNot){
        isLoading.setValue(isLoadingOrNot);
    }

    public void unHideSomething(String unHideItemId,int unhideType,final StringCallBack callback){
        setIsLoading(true);

        postRepo.unHideSomething(unHideItemId, unhideType, new StringCallBack() {
            @Override
            public void success(String item) {
                callback.success(item);
                setIsLoading(false);
            }

            @Override
            public void showError(String error) {
                callback.showError(error);
                setIsLoading(false);
            }
        });
    }

}
