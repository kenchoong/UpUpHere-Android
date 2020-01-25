package upuphere.com.upuphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.DecodeToken;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.repositories.UserRepo;

import com.google.android.material.navigation.NavigationView;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    public NavController navController;
    public NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigation();

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

    PrefManager prefManager;
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
}
