package upuphere.com.upuphere.user;

import androidx.appcompat.app.AppCompatActivity;
import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.MainActivity;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.helper.ApiHelper;
import upuphere.com.upuphere.helper.PrefManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity{

    EditText emailField,usernameField,passwordField,reenterPasswordField;
    Button createAccountButton;
    PrefManager prefManager;
    String phoneNumber = null;

    private static final int EMAIL_FIELD = 1;
    private static final int USERNAME_FIELD = 2;
    private static final int PASSWORD_FIELD = 3;
    private static final int REENTER_PASSWORD_FIELD = 4;

   //boolean isPasswordValid = ^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        prefManager = new PrefManager(getApplicationContext());


        if(getIntent().getExtras() != null){
             phoneNumber= getIntent().getExtras().getString("phone_number");
            Log.d("Phone Number",phoneNumber);
        }

        emailField = findViewById(R.id.emailField);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        reenterPasswordField = findViewById(R.id.reenterPasswordField);
        createAccountButton = findViewById(R.id.createAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = emailField.getText().toString();
                String username = usernameField.getText().toString();
                String passwordString = passwordField.getText().toString();

                createUserAccount(phoneNumber,emailString,passwordString,username);
            }
        });


        emailField.addTextChangedListener(credentialTextWatcher(emailField));
        usernameField.addTextChangedListener(credentialTextWatcher(usernameField));
        passwordField.addTextChangedListener(credentialTextWatcher(passwordField));
        reenterPasswordField.addTextChangedListener(credentialTextWatcher(reenterPasswordField));
    }

    private TextWatcher credentialTextWatcher(final EditText editText){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                switch (editText.getId()){
                    case R.id.emailField:
                        checkIfValidCredential(EMAIL_FIELD,emailField.getText().toString(),emailField);
                        break;
                    case R.id.usernameField:
                        checkIfValidCredential(USERNAME_FIELD,usernameField.getText().toString(),usernameField);
                        break;
                    case R.id.passwordField:
                        checkIfValidCredential(PASSWORD_FIELD,passwordField.getText().toString(),passwordField);
                        break;
                    case R.id.reenterPasswordField:
                        checkIfValidCredential(REENTER_PASSWORD_FIELD,reenterPasswordField.getText().toString(),reenterPasswordField);
                        break;
                }

            }
        };
    }

    private void checkIfValidCredential(int fieldTypes,String credential,EditText fields){
        switch (fieldTypes){
            case EMAIL_FIELD:
                if (!isEmailValid(credential)){
                    emailField.setError("Invalid email address");
                }

                checkIfCredentialExisted(credential,fields);
                break;
            case USERNAME_FIELD:
                if(credential.length() < 6){
                    fields.setError("Username must at least 6 characters");
                }
                Pattern pattern = Pattern.compile("^(?=.{6,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
                //username is 8-20 characters long
                //no _ or . at the beginning
                //no __ or _. or ._ or .. inside
                //no _ or . at the end
                //allowed a-z ,A-Z, 0-9, . and _

                boolean valid =  pattern.matcher(credential).matches();

                if(!valid){
                    fields.setError("Invalid username");
                }

                checkIfCredentialExisted(credential,fields);

                break;
            case PASSWORD_FIELD:
                if(credential.length()< 6){
                    fields.setError("Password must at least 6 characters");
                }
                break;

            case REENTER_PASSWORD_FIELD:
                //todo :: check for password and reenter is the same
                if(credential.length() < 6) {
                    fields.setError("Must at least 6 characters");
                }

                String passwordString = passwordField.getText().toString();

                if(!credential.equals(passwordString)){
                    fields.setError("Password doest match");
                }

                break;
        }
    }


    private void checkIfCredentialExisted(final String credential, final EditText fields){
        //call to server to check
        new ApiHelper().checkDetailsExisted(credential, 222,new BoolCallBack() {
            @Override
            public void success(boolean existed) {
                if(existed){
                    fields.setError(credential + " is taken");
                }
            }

            @Override
            public void showError(String error) {

            }
        });
    }




    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void createUserAccount(String phoneNumber,String emailString, String username, String passwordString) {
        //send to server

        new ApiHelper().signUpUser(getApplicationContext(), phoneNumber, emailString, username, passwordString, new CommonCallBack() {
            @Override
            public void success() {
                prefManager.setIsLoggedIn(true);
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
            }

            @Override
            public void showError(String error) {
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
            }
        });


    }
}
