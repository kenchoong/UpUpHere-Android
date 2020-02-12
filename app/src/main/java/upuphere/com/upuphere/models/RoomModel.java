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
