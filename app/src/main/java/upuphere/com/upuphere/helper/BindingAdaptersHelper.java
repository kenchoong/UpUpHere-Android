package upuphere.com.upuphere.helper;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.databinding.BindingAdapter;
import androidx.databinding.adapters.TextViewBindingAdapter;
import upuphere.com.upuphere.app.AppConfig;

public class BindingAdaptersHelper {

    @BindingAdapter("android:afterTextChanged")
    public static void setListener(TextView view, TextViewBindingAdapter.AfterTextChanged after) {
        setListener(view,after);
    }

    @BindingAdapter({"roomImageUrl"})
    public static void loadImage(ImageView view, String url){
        Glide.with(view.getContext())
                .load(AppConfig.PHOTO_ENDPOINT+url)
                .into(view);
    }

    @BindingAdapter({"timeAgo"})
    public static void setTimeAgo(TextView textView ,String createdAt){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date convertedDate = new Date();
        try{
            if(createdAt != null) {
                convertedDate = formatter.parse(createdAt);
                PrettyTime p =new PrettyTime();
                String datetime= p.format(convertedDate);
                textView.setText(datetime);
            }else{
                textView.setText("12asd123");
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
