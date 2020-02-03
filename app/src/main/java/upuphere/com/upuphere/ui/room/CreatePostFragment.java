package upuphere.com.upuphere.ui.room;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.master.permissionhelper.PermissionHelper;

import java.io.IOException;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.databinding.FragmentCreatePostBinding;
import upuphere.com.upuphere.fragment.DisplayPhotoFragment;
import upuphere.com.upuphere.fragment.PhotoBottomSheetDialogFragment;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.repositories.PostRepo;
import upuphere.com.upuphere.viewmodel.CreatePostViewModel;
import upuphere.com.upuphere.viewmodel.CreateRoomViewModel;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreatePostFragment extends Fragment implements CreatePostViewModel.CreatePostInterface {

    public static final String TAG = CreatePostFragment.class.getSimpleName();


    public CreatePostFragment() {
        // Required empty public constructor
    }

    FragmentCreatePostBinding binding;
    View rootView;
    CreatePostViewModel viewModel;
    String roomId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_post,container,false);
        viewModel = ViewModelProviders.of(requireActivity()).get(CreatePostViewModel.class);
        rootView = binding.getRoot();
        binding.setViewmodel(viewModel);
        setHasOptionsMenu(true);
        return rootView;
    }

    private Bitmap selectedPhoto = null;
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.setCreatePostStateInterface(this);

        roomId = CreatePostFragmentArgs.fromBundle(getArguments()).getRoomId();

        viewModel.getSelectedPhoto().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if(bitmap != null) {
                    selectedPhoto = bitmap;
                    binding.chooseImageButton.setVisibility(View.GONE);
                    binding.chosenImageShown.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).load(bitmap).into(binding.chosenImageShown);
                }else{
                    binding.chosenImageShown.setVisibility(View.GONE);
                    binding.chooseImageButton.setVisibility(View.VISIBLE);
                }
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    private static final int CAMERA_PROCESS = 100;
    private static final int GALLERY_PROCESS = 200;

    private void requestPermission(String[] permissionString, final int requestCode){
        PermissionHelper permissionHelper = new PermissionHelper(this, permissionString, requestCode);

        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted() called");
                switch (requestCode){
                    case CAMERA_PROCESS:
                        Log.d("PHOTO","CAMERA PERMISSION OK");
                        takePhotoFromCamera();
                        break;
                    case GALLERY_PROCESS:
                        Log.d("PHOTO"," CHOOSE PHOTO PERMISSION OK");
                        choosePhotoFromGallery();
                        break;
                }
            }

            @Override
            public void onIndividualPermissionGranted(String[] grantedPermission) {
                Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",",grantedPermission) + "]");
            }

            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "onPermissionDenied() called");
            }

            @Override
            public void onPermissionDeniedBySystem() {
                Log.d(TAG, "onPermissionDeniedBySystem() called");
            }
        });
    }

    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_PROCESS);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PROCESS);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.create_post){
            DecodeToken decodeToken = DecodeToken.newInstance();
            decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                @Override
                public void onTokenValid() {
                    Toast.makeText(getActivity(),"EVERYTHING IS OK",Toast.LENGTH_LONG).show();

                    //todo:: MAKE API call
                    PostRepo.getInstance().createPost(roomId, viewModel.statusText, photo, AppConfig.MEDIA_IS_PHOTO,
                    new StringCallBack() {
                        @Override
                        public void success(String item) {
                            Toast.makeText(getActivity(),"Post successfully created",Toast.LENGTH_LONG).show();

                            binding.statusField.setText("");
                            viewModel.getSelectedPhoto().setValue(null);

                            Navigation.findNavController(rootView).navigateUp();
                        }

                        @Override
                        public void showError(String error) {
                            Toast.makeText(getActivity(),error,Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onTokenAllInvalid() {

                }
            });

            decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
        }

        return super.onOptionsItemSelected(item);
    }

    Bitmap photo = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PROCESS){
            Log.d("Choose photo","OK");

            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    photo = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), contentURI);
                    viewModel.getSelectedPhoto().setValue(photo);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(requestCode == CAMERA_PROCESS && resultCode == RESULT_OK){
            Log.d("Take photo","Result ok ok");

            //Bitmap photo = null;
            if (data != null && data.getExtras() != null) {
                photo = (Bitmap) data.getExtras().get("data");
            }
            viewModel.getSelectedPhoto().setValue(photo);
        }
    }

    private PhotoBottomSheetDialogFragment photoBottomSheetDialogFragment;
    private void showBottomSheet(){
        photoBottomSheetDialogFragment = PhotoBottomSheetDialogFragment.newInstance();
        photoBottomSheetDialogFragment.setOptionClickListener(new PhotoBottomSheetDialogFragment.OptionClickListener() {
            @Override
            public void onTakePhoto() {
                photoBottomSheetDialogFragment.dismiss();
                requestPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PROCESS);
            }

            @Override
            public void onChooseFromAlbum() {
                photoBottomSheetDialogFragment.dismiss();
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GALLERY_PROCESS);
            }

            @Override
            public void onCancel() {
                photoBottomSheetDialogFragment.dismiss();
            }
        });

        photoBottomSheetDialogFragment.show(Objects.requireNonNull(getFragmentManager()),PhotoBottomSheetDialogFragment.TAG);
    }



    @Override
    public void onChosenImageClick() {
        NavDirections action = CreatePostFragmentDirections.actionCreatePostFragmentToDisplayPhotoFragment(photo, DisplayPhotoFragment.ORIGIN_CREATE_POST_FRAGMENT);
        Navigation.findNavController(rootView).navigate(action);
    }

    @Override
    public void onChooseImage() {
        showBottomSheet();
    }
}
