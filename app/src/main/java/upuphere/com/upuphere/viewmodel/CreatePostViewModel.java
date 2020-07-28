package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.fragment.PhotoBottomSheetDialogFragment;
import upuphere.com.upuphere.repositories.PostRepo;

public class CreatePostViewModel extends AndroidViewModel {

    //public String statusText;
    PostRepo postRepo;

    public MutableLiveData<String> statusText = new MutableLiveData<>("");

    public CreatePostViewModel(@NonNull Application application) {
        super(application);
        postRepo = new PostRepo(application);
    }

    public interface CreatePostInterface{
        void onChosenImageClick();
        void onChooseImage();
        void onLocationClicked();
        void onPostButtonClicked();
        void onStatusTextInserted();
    }

    public CreatePostInterface postInterface;

    public void setCreatePostStateInterface(CreatePostInterface stateInterface){
        this.postInterface = stateInterface;
    }


    public void onImageClick(View view){
        Log.d("IMAGE CLICK","CHOOSE IMAGE IS CLICKED");
        //createRoomState.setValue(CreateRoomState.SHOW_PHOTO_BOTTOM_SHEET);
        postInterface.onChooseImage();
    }

    public MutableLiveData<Bitmap> selectedPhoto;

    public MutableLiveData<Bitmap> getSelectedPhoto(){
        if(selectedPhoto ==null){
            selectedPhoto = new MutableLiveData<>();
        }
        return  selectedPhoto;
    }

    public void onClickImageChosen(View view){
        postInterface.onChosenImageClick();
    }

    public void onLocationClicked(View view){
        postInterface.onLocationClicked();
    }

    public void onPostButtonClicked(View view){
        postInterface.onPostButtonClicked();
    }

    public void createPost(String roomId, String statusText, Bitmap bitmap,String mediaType, final StringCallBack callBack){
        setIsLoading(true);
        postRepo.createPost(roomId, statusText, bitmap, mediaType, new StringCallBack() {
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

    public void afterStatusTextChanged(Editable e){
        postInterface.onStatusTextInserted();
    }

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<String> status = new MutableLiveData<>("");

    private void setIsLoading(boolean isLoadingOrNot){
        isLoading.setValue(isLoadingOrNot);
    }
}

