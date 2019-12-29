package upuphere.com.upuphere;

import androidx.appcompat.app.AppCompatActivity;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.libs.Authenticate;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.user.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    PrefManager prefManager;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(getApplicationContext());

        logoutButton = findViewById(R.id.logoutButton);


        if(!prefManager.isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String accessToken = prefManager.getUserAccessToken();

                if(accessToken != null){
                    Authenticate.revokedAccessToken(getApplicationContext(), new CommonCallBack() {
                        @Override
                        public void success() {
                            revokeRefreshToken();
                        }

                        @Override
                        public void showError(String error) {

                        }
                    });
                }

            }
        });
    }

    private void revokeRefreshToken(){
        Authenticate.revokeRefreshToken(getApplicationContext(), new CommonCallBack() {
            @Override
            public void success() {
                prefManager.removeAllSharedPrefrences();
                prefManager.setIsLoggedIn(false);
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }

            @Override
            public void showError(String error) {

            }
        });
    }

}
