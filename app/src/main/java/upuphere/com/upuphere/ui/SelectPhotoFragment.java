package upuphere.com.upuphere.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.master.permissionhelper.PermissionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.adapter.RoomAdapter;
import upuphere.com.upuphere.adapter.SelectPhotoAdapter;
import upuphere.com.upuphere.helper.SpacingItemDecoration;
import upuphere.com.upuphere.models.PhotoList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SelectPhotoFragment extends Fragment implements SelectPhotoAdapter.SelectPhotoListener, EasyPermissions.PermissionCallbacks {


    public SelectPhotoFragment() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    SelectPhotoAdapter selectPhotoAdapter;
    View rootView;
    LinearLayout grantAccessLinearLayout;
    Button grantAccessButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_select_photo, container, false);
        recyclerView = rootView.findViewById(R.id.selectPhotoRecyclerView);
        grantAccessLinearLayout = rootView.findViewById(R.id.grantAccessLinearLayout);
        grantAccessButton = rootView.findViewById(R.id.grantAccessButton);

        grantAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               requestUsingEasyPermission();
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(checkPermissionGranted(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
            showAllThePhotoInGallery();
        }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            disableRecyclerView();
        }

        requestUsingEasyPermission();
    }

    private void requestUsingEasyPermission(){
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, GALLERY_PROCESS, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .setRationale("UpUpHere need photo permission in order to let you post photo")
                        .setPositiveButtonText("Grant Permission")
                        .setNegativeButtonText("Not Now")
                        //.setTheme(R.style.my_fancy_style)
                        .build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private boolean checkPermissionGranted(Context mContext, String permissionString){
        return ContextCompat.checkSelfPermission(mContext,  permissionString) == PackageManager.PERMISSION_GRANTED;
    }

    private void showAllThePhotoInGallery(){
        grantAccessLinearLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        initRecyclerView();
        List<String> photoList = getAllShownImagesPath(getActivity());
        Log.d("PHOTO LIST", String.valueOf(photoList.size()));
        selectPhotoAdapter.setPhotoList(photoList);
    }

    private void disableRecyclerView(){
        recyclerView.setVisibility(View.GONE);
        grantAccessLinearLayout.setVisibility(View.VISIBLE);
    }

    public final String TAG = "Select photo";

    private static final int CAMERA_PROCESS = 100;
    private static final int GALLERY_PROCESS = 200;
    private static final int LOCATION_PROCESS = 300;

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }

    private void initRecyclerView() {


        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(mGridLayoutManager);

        recyclerView.addItemDecoration(new SpacingItemDecoration(3, dpToPx(4), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        selectPhotoAdapter = new SelectPhotoAdapter(this,getActivity());
        recyclerView.setAdapter(selectPhotoAdapter);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    @Override
    public void onSelectedPhoto() {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == GALLERY_PROCESS){
            showAllThePhotoInGallery();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(requestCode==GALLERY_PROCESS){
            disableRecyclerView();
        }
    }
}