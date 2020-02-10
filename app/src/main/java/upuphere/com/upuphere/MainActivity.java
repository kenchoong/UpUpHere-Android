package upuphere.com.upuphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.NotificationUtils;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.repositories.UserRepo;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    public NavController navController;
    public NavigationView navigationView;
    PrefManager prefManager;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager= new PrefManager(this);

        setupNavigation();

        requestFirebaseToken();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                 if (Objects.equals(intent.getAction(), AppConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    String postId = intent.getStringExtra("post_id");

                    Log.d("MAIN ACTIVITY", message);
                    Log.d("MAIN ACTIVITY", postId);
                }
            }
        };
    }

    private void requestFirebaseToken() {
        String myFirebaseToken = prefManager.getFirebaseToken();

        if(TextUtils.isEmpty(myFirebaseToken)){
           UserRepo.getInstance().requestTokenFromFirebase(this, new StringCallBack() {
               @Override
               public void success(String newToken) {
                   prefManager.setFirebaseToken(newToken);
               }

               @Override
               public void showError(String error) {
                   Log.d("Firebase error",error);
               }
           });
        }
    }

    private void setupNavigation() {

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        NavigationUI.setupWithNavController(navigationView, navController);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.profile:
                navController.navigate(R.id.profileFragment);
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
            prefManager.removeAllSharedPrefrences();
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

                        prefManager.removeAllSharedPrefrences();
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

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter(AppConfig.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications(this);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
