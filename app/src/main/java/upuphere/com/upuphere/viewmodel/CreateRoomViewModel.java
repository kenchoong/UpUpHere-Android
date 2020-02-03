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

public class CreateRoomViewModel extends ViewModel {

    @BindingAdapter({"bitmap"})
    public static void loadImage(ImageView view, Bitmap bitmap){
        Glide.with(view.getContext())
                .load(bitmap)
                .into(view);
    }

    public String roomName;
    public String statusText;

    public interface CreateRoomInterface{
        void onChosenImageClick();
        void onChooseImage();
    }

    private CreateRoomInterface roomInterface;

    public void setCreatePostStateInterface(CreateRoomInterface stateInterface){
        this.roomInterface = stateInterface;
    }


    public void onImageClick(View view){
        Log.d("IMAGE CLICK","CHOOSE IMAGE IS CLICKED");
        //createRoomState.setValue(CreateRoomState.SHOW_PHOTO_BOTTOM_SHEET);
        roomInterface.onChooseImage();
    }

    public MutableLiveData<Bitmap> selectedPhoto;

    public MutableLiveData<Bitmap> getSelectedPhoto(){
        if(selectedPhoto ==null){
            selectedPhoto = new MutableLiveData<>();
        }
        return  selectedPhoto;
    }

    public void onClickImageChosen(View view){
        roomInterface.onChosenImageClick();
    }

}
