package upuphere.com.upuphere.ui.user;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentResetPasswordBinding;
import upuphere.com.upuphere.repositories.UserRepo;
import upuphere.com.upuphere.viewmodel.LoginViewModel;
import upuphere.com.upuphere.viewmodel.ResetPasswordViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment implements ResetPasswordViewModel.onResetPasswordListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PHONE_NUMBER = "phone_number";


    // TODO: Rename and change types of parameters
    private String phoneNumberString;
    View rootView;
    FragmentResetPasswordBinding binding;
    ResetPasswordViewModel viewModel;
    LoginViewModel loginViewModel;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneNumberString = getArguments().getString(PHONE_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_reset_password,container,false);
        rootView = binding.getRoot();
        viewModel = ViewModelProviders.of(requireActivity()).get(ResetPasswordViewModel.class);
        loginViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        binding.setViewmodel(viewModel);

        initializeProgressBar();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.setOnResetPasswordListener(this);
    }

    @Override
    public void onPasswordTooShort() {
        binding.newPassword.setError("Password must at least 6 character");
    }

    @Override
    public void onFieldMatch() {
        updateUserPassword();
    }

    private void updateUserPassword() {
        viewModel.setIsLoading(true);
        UserRepo.getInstance().updateUserPassword(phoneNumberString, viewModel.confirmNewPassword, new StringCallBack() {
            @Override
            public void success(String item) {
                Log.d("RESET Password",item);
                viewModel.setIsLoading(false);

                loginViewModel.backToLogin();
                NavDirections action = ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment2();
                Navigation.findNavController(rootView).navigate(action);

            }

            @Override
            public void showError(String error) {
                Log.d("Reset error",error);
                viewModel.setIsLoading(false);
                binding.statusText.setText(R.string.failed_reset_password);
            }
        });
    }

    private void initializeProgressBar() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    if(binding.statusText.getVisibility() == View.VISIBLE){
                        binding.statusText.setVisibility(View.GONE);
                    }

                    binding.progressBar3.setVisibility(View.VISIBLE);
                }
                else{
                    binding.progressBar3.setVisibility(View.GONE);
                }
            }
        });

    }


    @Override
    public void onFieldIsEmpty(int whichField) {
        switch (whichField){
            case ResetPasswordViewModel.NEW_PASSWORD_FIELD_EMPTY:
                binding.newPassword.setError("This field cannot be empty");
                break;
            case ResetPasswordViewModel.CONFIRM_PASSWORD_FIELD_EMPTY:
                binding.confirmNewPassword.setError("This field cannot be empty");
                break;
        }
    }

    @Override
    public void onFieldNotMatch() {
        binding.confirmNewPassword.setError("Password not matched");
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.newPassword.setText("");
        binding.confirmNewPassword.setText("");
    }
}
