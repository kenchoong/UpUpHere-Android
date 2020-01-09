package upuphere.com.upuphere.ui.user;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Pattern;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.libs.Authenticate;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private EditText emailField,usernameField,passwordField,reenterPasswordField;
    private PrefManager prefManager;
    private String phoneNumber = null;

    private static final int EMAIL_FIELD = 1;
    private static final int USERNAME_FIELD = 2;
    private static final int PASSWORD_FIELD = 3;
    private static final int REENTER_PASSWORD_FIELD = 4;


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view1 = inflater.inflate(R.layout.fragment_sign_up, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
        /*
        Button button = view1.findViewById(R.id.next);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.mainFragment);
            }
        });
        */
        return view1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumber = SignUpFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getPhoneNumber();
        Log.d("PHONE_NUMBER",phoneNumber);

        prefManager = new PrefManager(getActivity());

        emailField = view.findViewById(R.id.emailField);
        usernameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);
        reenterPasswordField = view.findViewById(R.id.reenterPasswordField);
        Button createAccountButton = view.findViewById(R.id.createAccountButton);
        TextView redirectLoginText = view.findViewById(R.id.redirect_login_text);

        redirectLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.loginFragment);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = emailField.getText().toString();
                String username = usernameField.getText().toString();
                String passwordString = passwordField.getText().toString();

                createUserAccount(view,phoneNumber,emailString,passwordString,username);
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
        Authenticate.checkDetailsExisted(getContext(),credential, 222,new BoolCallBack() {
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

    private void createUserAccount(final View view, String phoneNumber, String emailString, String username, String passwordString) {
        //send to server
        Authenticate.signUp(getActivity(), phoneNumber, emailString, username, passwordString, new CommonCallBack() {
            @Override
            public void success() {
                prefManager.setIsLoggedIn(true);
                //startActivity(new Intent(SignUpActivity.this, MainActivity.class));

                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.mainFragment);
            }

            @Override
            public void showError(String error) {
                Toast.makeText(getActivity(),error,Toast.LENGTH_LONG).show();
            }
        });


    }
}
