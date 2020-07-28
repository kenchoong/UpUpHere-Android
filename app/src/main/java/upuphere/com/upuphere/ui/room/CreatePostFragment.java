package upuphere.com.upuphere.ui.room;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.master.permissionhelper.PermissionHelper;

import java.io.IOException;
import java.security.Key;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import upuphere.com.upuphere.fragment.SignUpBottomSheet;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.KeyboardHelper;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.repositories.PostRepo;
import upuphere.com.upuphere.ui.onboarding.AgreementFragment;
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

    MenuItem create;
    PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_post,container,false);
        viewModel = new ViewModelProvider(requireActivity()).get(CreatePostViewModel.class);
        rootView = binding.getRoot();
        binding.setViewmodel(viewModel);
        setHasOptionsMenu(true);

        prefManager = new PrefManager(getActivity());

        // todo:: clean this code in a function
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        KeyboardHelper.showKeyboard(getActivity());
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        /*
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(rootView).navigateUp();
            }
        });
        */

        return rootView;
    }

    private Bitmap selectedPhoto = null;
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.setCreatePostStateInterface(this);

        roomId = CreatePostFragmentArgs.fromBundle(getArguments()).getRoomId();





        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(isLoadingNow){
                    Toast.makeText(getActivity(),"Creating post",Toast.LENGTH_LONG).show();
                }else{
                    clearState();
                    Navigation.findNavController(view).navigateUp();
                }
            }
        });
    }

    private static final int CAMERA_PROCESS = 100;
    private static final int GALLERY_PROCESS = 200;
    private static final int LOCATION_PROCESS = 300;

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
                    case LOCATION_PROCESS:
                        Log.d("LOCATION", "Permission granted");
                        //todo :: get the name of the location he in
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

    Button postButton;
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        /*
        inflater.inflate(R.menu.menu_post,menu);
        create = menu.findItem(R.id.create_post);

        initializeProgressBar();
        super.onCreateOptionsMenu(menu, inflater);
         */
        //MenuInflater menuInflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);

        MenuItem getItem = menu.findItem(R.id.create_post);
        if (getItem != null) {

            postButton = (Button) getItem.getActionView();
            disablePostButton();
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("CREATE POST", "Create post clicked");
                    Log.d("Status String", viewModel.statusText.getValue() );
                    //createPostToServer();
                    viewModel.postInterface.onPostButtonClicked();
                }
            });

        }
        observeStatus();
        obServerPhoto();
    }
/*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.create_post){
            DecodeToken decodeToken = DecodeToken.newInstance();
            decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
                @Override
                public void onTokenValid() {
                    Toast.makeText(getActivity(),"EVERYTHING IS OK",Toast.LENGTH_LONG).show();

                    createPostToServer();
                }

                @Override
                public void onTokenAllInvalid() {

                }
            });

            decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
        }

        return super.onOptionsItemSelected(item);
    }
*/
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
    public void onLocationClicked() {
        requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_PROCESS);
    }

    private void enablePostButton(){
        postButton.setEnabled(true);
        postButton.setBackground(getResources().getDrawable(R.drawable.pill_background));
    }

    private void disablePostButton(){
        postButton.setEnabled(false);
        postButton.setBackgroundColor(getResources().getColor(R.color.secondaryTextColor));
    }

    @Override
    public void onPostButtonClicked() {

        if(!prefManager.isLoggedIn()){
            hideKeyBoard();
            showSignUpBottomSheet();
        }else{
            if(!prefManager.isPhoneVerified()){
                Bundle bundle = new Bundle();
                bundle.putInt("previousFragmentCode",0);
                Navigation.findNavController(rootView).navigate(R.id.phoneAuthFragment3,bundle);
            }else{
                createPost();
            }
        }
    }

    @Override
    public void onStatusTextInserted() {
        if(TextUtils.isEmpty(binding.statusField.getText().toString())){
            disablePostButton();
        }
        enablePostButton();
    }

    private void createPost(){
        DecodeToken decodeToken = DecodeToken.newInstance();
        decodeToken.setOnTokenListener(new DecodeToken.onTokenListener() {
            @Override
            public void onTokenValid() {
                Toast.makeText(getActivity(),"EVERYTHING IS OK",Toast.LENGTH_LONG).show();

                createPostToServer();
            }

            @Override
            public void onTokenAllInvalid() {

            }
        });

        decodeToken.checkAccessTokenRefreshTokenIfExpired(getActivity());
    }

    private void showSignUpBottomSheet(){
        final SignUpBottomSheet signUpBottomSheet = SignUpBottomSheet.newInstance();
        signUpBottomSheet.setOnSignUpSheetInterface(new SignUpBottomSheet.OnSignUpSheetInterface() {
            @Override
            public void onCreateButtonClicked() {
                signUpBottomSheet.dismiss();
                Navigation.findNavController(rootView).navigate(R.id.signUpFragment);
            }

            @Override
            public void onTermClicked() {
                signUpBottomSheet.dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt("agreementType", AgreementFragment.TERM_OF_USE);
                Navigation.findNavController(rootView).navigate(R.id.agreementFragment, bundle);
            }

            @Override
            public void onLoginClicked() {
                signUpBottomSheet.dismiss();
                Navigation.findNavController(rootView).navigate(R.id.loginFragment);
            }

            @Override
            public void onPrivacyClicked() {
                signUpBottomSheet.dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt("agreementType", AgreementFragment.PRIVACY_POLICY);
                Navigation.findNavController(rootView).navigate(R.id.agreementFragment, bundle);
            }
        });
        signUpBottomSheet.show(getActivity().getSupportFragmentManager(), SignUpBottomSheet.TAG);
    }

    @Override
    public void onChosenImageClick() {
        NavDirections action = CreatePostFragmentDirections.actionCreatePostFragmentToDisplayPhotoFragment(photo, DisplayPhotoFragment.ORIGIN_CREATE_POST_FRAGMENT);
        Navigation.findNavController(rootView).navigate(action);
    }

    @Override
    public void onChooseImage() {
        hideKeyBoard();
        //showBottomSheet();
        Navigation.findNavController(rootView).navigate(R.id.selectPhotoFragment);
    }

    private boolean isLoadingNow = false;

    private void clearState() {
        binding.statusField.setText("");
        viewModel.getSelectedPhoto().setValue(null);
        hideKeyBoard();
    }

    private void initializeProgressBar() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    isLoadingNow = true;
                    disableMenuItem(create);
                    disableAllUI();
                    binding.progressBar4.setVisibility(View.VISIBLE);
                }
                else{
                    isLoadingNow = false;
                    binding.progressBar4.setVisibility(View.GONE);
                    enableMenuItem(create);
                    enableAllUi();
                }
            }
        });
    }

    private void enableAllUi() {
        binding.statusField.setEnabled(true);
        if(binding.chosenImageShown.getVisibility() == View.VISIBLE){
            binding.chosenImageShown.setClickable(true);
        }
/*
        if(binding.chooseImageButton.getVisibility() == View.VISIBLE){
            binding.chooseImageButton.setClickable(true);
        }*/
    }

    private void disableAllUI() {
        binding.statusField.setEnabled(false);
        if(binding.chosenImageShown.getVisibility() == View.VISIBLE){
            binding.chosenImageShown.setClickable(false);
        }
/*
        if(binding.chooseImageButton.getVisibility() == View.VISIBLE){
            binding.chooseImageButton.setClickable(false);
        }*/
    }

    private void disableMenuItem(MenuItem menuItem) {
        menuItem.setEnabled(false);
        String title = menuItem.getTitle().toString();
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey_color)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
        menuItem.setTitle(s);
    }

    private void enableMenuItem(MenuItem menuItem) {
        menuItem.setEnabled(true);
        String title = menuItem.getTitle().toString();
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.background_color)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
        menuItem.setTitle(s);
    }
    private void observeStatus(){
        viewModel.statusText.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if(TextUtils.isEmpty(status)){
                    disablePostButton();
                }else{
                    enablePostButton();
                }
            }
        });
    }

    private void obServerPhoto(){
        viewModel.getSelectedPhoto().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if(bitmap != null) {
                    selectedPhoto = bitmap;
                    //binding.chooseImageButton.setVisibility(View.GONE);
                    binding.chosenImageShown.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).load(bitmap).into(binding.chosenImageShown);
                    enablePostButton();
                }else{
                    binding.chosenImageShown.setVisibility(View.GONE);
                    //binding.chooseImageButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void createPostToServer() {
        if(viewModel.statusText.getValue().equals("")){
            Log.d("status string 123", "no value");
        }
        Log.d("status string 123", viewModel.statusText.getValue());
        Log.d("photo string 123", String.valueOf(photo));
        //todo:: MAKE API call
        viewModel.createPost(roomId, viewModel.statusText.getValue() , photo, AppConfig.MEDIA_IS_PHOTO,
                new StringCallBack() {
                    @Override
                    public void success(String item) {
                        Toast.makeText(getActivity(),"Post successfully created",Toast.LENGTH_LONG).show();

                        clearState();

                        Navigation.findNavController(rootView).navigateUp();
                    }

                    @Override
                    public void showError(String error) {
                        Toast.makeText(getActivity(),error,Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        clearState();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
    }

    private void hideKeyBoard(){
        KeyboardHelper.hideKeyboard(getActivity());
    }
}
