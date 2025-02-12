package upuphere.com.upuphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import androidx.appcompat.widget.Toolbar;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.NotificationUtils;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.models.NotificationModel;
import upuphere.com.upuphere.repositories.UserRepo;
import upuphere.com.upuphere.ui.onboarding.AgreementFragment;
import upuphere.com.upuphere.viewmodel.NotificationViewModel;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public DrawerLayout drawerLayout;
    public NavController navController;
    public NavigationView navigationView;
    PrefManager prefManager;

    //private BroadcastReceiver mRegistrationBroadcastReceiver;
    NotificationViewModel viewModel;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        prefManager= new PrefManager(this);

        setupNavigation();

        initializeAdmob();

        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
    }

    private void initializeAdmob() {
        if(BuildConfig.DEBUG) {
            Log.d("NOW IS","DEBUG");
            List<String> testDeviceIds = Arrays.asList("F06871E55E94CE9D1DBAF1B7AF921A42");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    private void setupNavigation() {

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        NavigationUI.setupWithNavController(toolbar,navController,drawerLayout);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this,R.id.nav_host_fragment),drawerLayout);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Log.i(TAG, "onNewIntent() called.");
        if (intent != null && intent.getExtras() != null && intent.getExtras().size() > 0) {
            Log.d(TAG, "Received extras in onNewIntent()");

            String message = intent.getStringExtra("message");
            String timestamp = intent.getStringExtra("timestamp");
            String post_id = intent.getStringExtra("post_id");

            NotificationModel notification = new NotificationModel(message,timestamp,post_id);
            viewModel.deleteNotification(notification);

            Bundle args = new Bundle();
            args.putString("postId",post_id);
            navController.navigate(R.id.singlePostFragment,args);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {
            /*
            case R.id.profile:
                navController.navigate(R.id.profileFragment);
                break;
            */
            case R.id.feedback:
                Bundle bundle = new Bundle();
                bundle.putInt("agreementType", AgreementFragment.FEEDBACK_FORM);
                navController.navigate(R.id.agreementFragment, bundle);
                break;
            case R.id.logout:
                logoutUser();
                break;

        }
        return true;

    }


    private void logoutUser() {
        prefManager = new PrefManager(this);

        String accessToken = prefManager.getUserAccessToken();
        String refreshToken = prefManager.getUserRefreshToken();

        if(accessToken == null && refreshToken == null){
            prefManager.setIsLoggedIn(false);
            navController.navigate(R.id.loginFragment);
        }
        else if(!DecodeToken.jwtTokenStillValid(accessToken) && !DecodeToken.jwtTokenStillValid(refreshToken)){
            //go to login
            navController.navigate(R.id.loginFragment);
        }else if(!DecodeToken.jwtTokenStillValid(accessToken) && DecodeToken.jwtTokenStillValid(refreshToken)){
            //get the new access token
            UserRepo.getInstance().getRefreshAccessToken(new StringCallBack() {
                @Override
                public void success(String item) {
                    revokedAccess();
                }

                @Override
                public void showError(String error) {

                }
            });
        }else{
            revokedAccess();
        }

    }

    private void revokedAccess(){
        UserRepo.getInstance().revokedAccessToken(new CommonCallBack() {
            @Override
            public void success() {
                Log.d("Revoked access","Successful");
                UserRepo.getInstance().revokeRefreshToken(new CommonCallBack() {
                    @Override
                    public void success() {
                        Log.d("Revoked refresh","Successful");

                        prefManager.setIsLoggedIn(false);
                        navController.navigate(R.id.loginFragment);
                    }

                    @Override
                    public void showError(String error) {

                    }
                });
            }
            @Override
            public void showError(String error) {

            }
        });
    }


}
