package upuphere.com.upuphere.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import upuphere.com.upuphere.R;

public class PhotoBottomSheetDialogFragment extends BottomSheetDialogFragment
        implements View.OnClickListener {

    public static final String TAG = "PhotoBottomDialog";

    private OptionClickListener mListener;

    public interface OptionClickListener {
        void onTakePhoto();
        void onChooseFromAlbum();
        void onCancel();
    }

    public static PhotoBottomSheetDialogFragment newInstance() {
        return new PhotoBottomSheetDialogFragment();
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.takePhoto).setOnClickListener(this);
        view.findViewById(R.id.chooseFromAlbum).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
    }

    public void setOptionClickListener(OptionClickListener listener){
        this.mListener = listener;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.takePhoto:
                mListener.onTakePhoto();
                break;
            case R.id.chooseFromAlbum:
                mListener.onChooseFromAlbum();
                break;
            case R.id.cancel:
                mListener.onCancel();
                break;
        }
    }




}
