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

import com.bumptech.glide.Glide;

import java.util.Objects;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.viewmodel.CreatePostViewModel;
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
    CreateRoomViewModel createRoomViewModel;
    CreatePostViewModel createPostViewModel;
    View rootView;
    int originFragment;

    public static final int ORIGIN_CREATE_ROOM_FRAGMENT = 1111;
    public static final int ORIGIN_CREATE_POST_FRAGMENT = 2222;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_display_photo, container, false);

        createRoomViewModel = new ViewModelProvider(requireActivity()).get(CreateRoomViewModel.class);
        createPostViewModel = new ViewModelProvider(requireActivity()).get(CreatePostViewModel.class);
        selectedImage = rootView.findViewById(R.id.selectedImage);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Bitmap selectedPhoto = DisplayPhotoFragmentArgs.fromBundle(getArguments()).getBitmap();
        originFragment = DisplayPhotoFragmentArgs.fromBundle(getArguments()).getOriginFragment();
        Log.d("SELECTED PHOTO 123",selectedPhoto.toString());

        Glide.with(getActivity()).load(selectedPhoto).into(selectedImage);

        final NavController navController = Navigation.findNavController(view);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed(){
                Log.d("DISPLAY PHOTO","BACK PRESSED");
                //createRoomViewModel.backToCreateRoom();
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

        switch (originFragment){
            case ORIGIN_CREATE_ROOM_FRAGMENT:
                createRoomViewModel.getSelectedPhoto().setValue(null);
                break;
            case ORIGIN_CREATE_POST_FRAGMENT:
                createPostViewModel.getSelectedPhoto().setValue(null);
                break;
        }
        Navigation.findNavController(rootView).navigateUp();
    }

    @Override
    public void onCancelClick() {
        deleteFragment.dismiss();
    }
}
