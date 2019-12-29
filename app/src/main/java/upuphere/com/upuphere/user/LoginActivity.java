package upuphere.com.upuphere.user;

import androidx.appcompat.app.AppCompatActivity;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.MainActivity;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.libs.Authenticate;
import upuphere.com.upuphere.helper.PrefManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText identityField,passwordField;
    Button loginButton;
    TextView redirectText;

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        identityField = findViewById(R.id.identityField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        redirectText = findViewById(R.id.redirectText);

        prefManager = new PrefManager(getApplicationContext());

        redirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PhoneAuthenticationActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String identityString = identityField.getText().toString();
                String passwordString = passwordField.getText().toString();

                loginUser(getApplicationContext(),identityString,passwordString);
            }
        });

    }

    private void loginUser(Context mContext,String identityString, String passwordString) {

        Authenticate.login(mContext, identityString, passwordString, new CommonCallBack() {
            @Override
            public void success() {
                prefManager.setIsLoggedIn(true);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void showError(String error) {
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
            }
        });




    }


}
