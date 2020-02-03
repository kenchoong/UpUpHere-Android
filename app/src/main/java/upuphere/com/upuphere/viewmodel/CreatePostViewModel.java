package upuphere.com.upuphere.viewmodel;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.fragment.PhotoBottomSheetDialogFragment;

public class CreatePostViewModel extends ViewModel {

    public String statusText;

    public interface CreatePostInterface{
        void onChosenImageClick();
        void onChooseImage();
    }

    private CreatePostInterface postInterface;

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

}

