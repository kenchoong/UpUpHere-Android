package upuphere.com.upuphere.helper;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.adapters.TextViewBindingAdapter;

public class EditTextBindingAdapters {

    @BindingAdapter("android:afterTextChanged")
    public static void setListener(TextView view, TextViewBindingAdapter.AfterTextChanged after) {
        setListener(view,after);
    }
}
