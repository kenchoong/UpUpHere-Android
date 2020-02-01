package upuphere.com.upuphere.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.viewmodel.CreateRoomViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayPhotoFragment extends Fragment implements DeletePhotoBottomSheetDialogFragment.OnDeleteOptionClickListener{

    public static final String TAG = DisplayPhotoFragment.class.getSimpleName();

    public DisplayPhotoFragment() {
        // Required empty public constructor
    }


    private ImageView selectedImage;
    CreateRoomViewModel viewModel;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_display_photo, container, false);

        viewModel = ViewModelProviders.of(requireActivity()).get(CreateRoomViewModel.class);
        selectedImage = rootView.findViewById(R.id.selectedImage);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Bitmap selectedPhoto = DisplayPhotoFragmentArgs.fromBundle(getArguments()).getBitmap();
        Log.d("SELECTED PHOTO 123",selectedPhoto.toString());

        Glide.with(getActivity()).load(selectedPhoto).into(selectedImage);

        final NavController navController = Navigation.findNavController(view);

        viewModel.createRoomState.observe(getViewLifecycleOwner(), new Observer<CreateRoomViewModel.CreateRoomState>() {
            @Override
            public void onChanged(CreateRoomViewModel.CreateRoomState createRoomState) {
                if (createRoomState == CreateRoomViewModel.CreateRoomState.BACK_TO_CREATE_ROOM) {
                    navController.popBackStack(R.id.createRoomFragment, false);
                }
            }
        });



        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed(){
                Log.d("DISPLAY PHOTO","BACK PRESSED");
                viewModel.backToCreateRoom();
                //navController.popBackStack(R.id.createRoomFragment,false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete_icon,menu);
    }

    DeletePhotoBottomSheetDialogFragment deleteFragment;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete){
            deleteFragment = DeletePhotoBottomSheetDialogFragment.newInstance();
            deleteFragment.setDeleteListener(this);
            deleteFragment.show(Objects.requireNonNull(getFragmentManager()),TAG);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteClick() {
        deleteFragment.dismiss();
        viewModel.getSelectedPhoto().setValue(null);
        viewModel.backToCreateRoom();
    }

    @Override
    public void onCancelClick() {
        deleteFragment.dismiss();
    }
}
