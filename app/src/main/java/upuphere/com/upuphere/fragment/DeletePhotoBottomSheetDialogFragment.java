package upuphere.com.upuphere.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import upuphere.com.upuphere.R;

public class DeletePhotoBottomSheetDialogFragment extends BottomSheetDialogFragment
        implements View.OnClickListener {


    public static DeletePhotoBottomSheetDialogFragment newInstance(){
        return new DeletePhotoBottomSheetDialogFragment();
    }

    private OnDeleteOptionClickListener deleteListener;

    public interface OnDeleteOptionClickListener{
        void onDeleteClick();
        void onCancelClick();
    }

    public void setDeleteListener(OnDeleteOptionClickListener mListener){
        this.deleteListener = mListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.delete_photo_bottom_sheet,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.deleteButton).setOnClickListener(this);
        view.findViewById(R.id.cancelButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.deleteButton:
                deleteListener.onDeleteClick();
                break;
            case R.id.cancelButton:
                deleteListener.onCancelClick();
                break;
        }
    }
}
