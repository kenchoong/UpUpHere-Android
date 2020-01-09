package upuphere.com.upuphere.ui.user;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.MainActivity;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.libs.Authenticate;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }

    EditText identityField,passwordField;
    Button loginButton;
    TextView redirectText;
    PrefManager prefManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();

        prefManager = new PrefManager(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        identityField = view.findViewById(R.id.identityField);
        passwordField = view.findViewById(R.id.passwordField);
        loginButton = view.findViewById(R.id.loginButton);
        redirectText = view.findViewById(R.id.redirectText);

        redirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = LoginFragmentDirections.actionLoginFragmentToRegistrationGraph();
                Navigation.findNavController(view).navigate(action);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String identityString = identityField.getText().toString();
                String passwordString = passwordField.getText().toString();

                loginUser(getActivity(),identityString,passwordString);
            }
        });

    }

    private void loginUser(final Context mContext, String identityString, String passwordString) {

        Authenticate.login(mContext, identityString, passwordString, new CommonCallBack() {
            @Override
            public void success() {
                prefManager.setIsLoggedIn(true);
                NavController navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.fragment);
                navController.navigate(R.id.mainFragment);
            }

            @Override
            public void showError(String error) {
                Toast.makeText(getActivity(),error,Toast.LENGTH_LONG).show();
            }
        });
    }



}
