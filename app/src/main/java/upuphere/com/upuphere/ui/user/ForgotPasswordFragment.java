package upuphere.com.upuphere.ui.user;


import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentForgotPasswordBinding;
import upuphere.com.upuphere.viewmodel.ForgotPasswordViewModel;
import upuphere.com.upuphere.viewmodel.LoginViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment implements ForgotPasswordViewModel.ForgotPassInterface{

    FragmentForgotPasswordBinding binding;
    ForgotPasswordViewModel viewModel;
    View rootView;
    LoginViewModel loginViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_forgot_password,container,false);
        viewModel = ViewModelProviders.of(requireActivity()).get(ForgotPasswordViewModel.class);
        loginViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        rootView =binding.getRoot();
        binding.setViewmodel(viewModel);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                loginViewModel.backToLogin();
                Navigation.findNavController(view).navigateUp();
            }
        });

    }

    @Override
    public void onResetPasswordButtonClick() {

    }
}
