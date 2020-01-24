package upuphere.com.upuphere.models;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.databinding.BindingAdapter;
import upuphere.com.upuphere.app.AppConfig;

public class RoomModel{

    @BindingAdapter({"roomImageUrl"})
    public static void loadImage(ImageView view,String url){
        Glide.with(view.getContext())
                .load(AppConfig.PHOTO_ENDPOINT+url)
                .into(view);
    }

    @SerializedName("all_rooms")
    @Expose
    private List<AllRooms> allRooms = null;

    public List<AllRooms> getAllRooms() {
        return allRooms;
    }

    public void setAllRooms(List<AllRooms> allRooms) {
        this.allRooms = allRooms;
    }
}
