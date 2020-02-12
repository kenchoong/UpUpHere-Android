package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
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
import upuphere.com.upuphere.fragment.PhotoBottomSheetDialogFragment;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.repositories.RoomRepo;

public class CreateRoomViewModel extends AndroidViewModel {
    private RoomRepo roomRepo;

    public CreateRoomViewModel(@NonNull Application application) {
        super(application);
        roomRepo = new RoomRepo(application);
    }

    @BindingAdapter({"bitmap"})
    public static void loadImage(ImageView view, Bitmap bitmap){
        Glide.with(view.getContext())
                .load(bitmap)
                .into(view);
    }

    public String roomName;
    //public String statusText;

    public interface CreateRoomInterface{
        void onChosenImageClick();
        void onChooseImage();
    }

    private CreateRoomInterface roomInterface;

    public void setCreatePostStateInterface(CreateRoomInterface stateInterface){
        this.roomInterface = stateInterface;
    }

    public interface CreateRoomListener{
        void onCreatedRoom(AllRooms rooms);
    }


    public void createRoom(final String roomName, Bitmap photo, final CreateRoomListener listener){
        roomRepo.createRoom(roomName, photo, new StringCallBack() {
            @Override
            public void success(String id) {
                AllRooms room = new AllRooms();
                room.setId(id);
                room.setRoomName(roomName);

                listener.onCreatedRoom(room);
            }

            @Override
            public void showError(String error) {
                listener.onCreatedRoom(null);
            }
        });
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
