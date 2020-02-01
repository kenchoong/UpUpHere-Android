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

    public enum CreateRoomState{
        EXIT_CREATE_ROOM_PROCESS,
        SHOW_PHOTO_BOTTOM_SHEET,
        DISPLAY_CHOOSEN_IMAGE,
        SUCCESSFUL_CREATE_ROOM,
        BACK_TO_CREATE_ROOM
    }

    public MutableLiveData<CreateRoomState> createRoomState =  new MutableLiveData<>();


    public void exitCreateRoom(){
        createRoomState.setValue(CreateRoomState.EXIT_CREATE_ROOM_PROCESS);
    }

    public void onImageClick(View view){
        Log.d("IMAGE CLICK","CHOOSE IMAGE IS CLICKED");
        createRoomState.setValue(CreateRoomState.SHOW_PHOTO_BOTTOM_SHEET);
    }

    public MutableLiveData<Bitmap> selectedPhoto;

    public MutableLiveData<Bitmap> getSelectedPhoto(){
        if(selectedPhoto ==null){
            selectedPhoto = new MutableLiveData<>();
        }
        return  selectedPhoto;
    }

    public void onClickImageChosen(View view){
        createRoomState.setValue(CreateRoomState.DISPLAY_CHOOSEN_IMAGE);
    }

    public void backToCreateRoom(){
        createRoomState.setValue(CreateRoomState.BACK_TO_CREATE_ROOM);
    }

}
