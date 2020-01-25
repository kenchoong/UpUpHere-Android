package upuphere.com.upuphere.models;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.databinding.BindingAdapter;
import upuphere.com.upuphere.app.AppConfig;

public class RoomModel{

    @BindingAdapter({"roomImageUrl"})
    public static void loadImage(ImageView view,String url){
        Glide.with(view.getContext())
                .load(AppConfig.PHOTO_ENDPOINT+url)
                .into(view);
    }

    @BindingAdapter({"timeAgo"})
    public static void setTimeAgo(TextView textView ,String createdAt){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date convertedDate = new Date();
        try{
            convertedDate = formatter.parse(createdAt);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PrettyTime p =new PrettyTime();
        String datetime= p.format(convertedDate);
        textView.setText(datetime);
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
