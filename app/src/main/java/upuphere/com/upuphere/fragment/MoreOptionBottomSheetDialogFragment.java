package upuphere.com.upuphere.fragment;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import upuphere.com.upuphere.R;

public class MoreOptionBottomSheetDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    public static final String TAG = Application.class.getSimpleName();

    private OnOptionListener listener;

    public interface OnOptionListener{
        void onBlockUser();
        void onHide();
        void onReport();
        void onCancel();
    }

    public void setOnOptionListener(OnOptionListener listener) {
        this.listener = listener;
    }

    public static MoreOptionBottomSheetDialogFragment newInstance(){
        return new MoreOptionBottomSheetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.more_option_bottom_sheet,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.hide).setOnClickListener(this);
        view.findViewById(R.id.block).setOnClickListener(this);
        view.findViewById(R.id.report).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.hide:
                listener.onHide();
                break;
            case R.id.block:
                listener.onBlockUser();
                break;
            case R.id.report:
                listener.onReport();
                break;
            case R.id.cancel:
                listener.onCancel();
                break;
        }
    }
}
