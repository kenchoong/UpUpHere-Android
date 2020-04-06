package upuphere.com.upuphere.ui.user;


import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentForgotPasswordBinding;
import upuphere.com.upuphere.helper.KeyboardHelper;
import upuphere.com.upuphere.repositories.UserRepo;
import upuphere.com.upuphere.viewmodel.ForgotPasswordViewModel;
import upuphere.com.upuphere.viewmodel.LoginViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment implements ForgotPasswordViewModel.ForgotPassInterface{

    public static final String TAG = ForgotPasswordFragment.class.getSimpleName();

    FragmentForgotPasswordBinding binding;
    ForgotPasswordViewModel viewModel;
    View rootView;
    LoginViewModel loginViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_forgot_password,container,false);
        viewModel = new ViewModelProvider(requireActivity()).get(ForgotPasswordViewModel.class);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        rootView =binding.getRoot();
        binding.setViewmodel(viewModel);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.setForgotPassInterface(this);

        initializeProgressBar();
        observeNetworkCallStatus();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).navigateUp();
            }
        });

    }

    private void observeNetworkCallStatus() {
        viewModel.status.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if(!TextUtils.isEmpty(status)){
                    binding.statusText.setText(status);
                }
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

                    binding.progressBar2.setVisibility(View.VISIBLE);
                }
                else{
                    binding.progressBar2.setVisibility(View.GONE);
                }
            }
        });

    }

    private void disableSendButton(){
        binding.sendButton.setAlpha(0.5f);
        binding.sendButton.setEnabled(false);
    }

    private void enableSendButton(){
        binding.statusText.setVisibility(View.GONE);
        binding.sendButton.setAlpha(1);
        binding.sendButton.setEnabled(true);
    }

    @Override
    public void onUsingPhoneInstead() {
        NavDirections directions = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToPhoneAuthFragment3(PhoneAuthFragment3.FROM_FORGOT_PASSWORD);
        Navigation.findNavController(rootView).navigate(directions);
    }

    @Override
    public void onInvalidEmail() {
        binding.email.setError("Invalid Email");
        disableSendButton();
    }

    @Override
    public void onEmailSent() {
        disableSendButton();
        binding.statusText.setText(R.string.password_reset_email_sent);
        binding.statusText.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.statusText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEmailNotRegistered() {
        binding.statusText.setText(R.string.email_not_registered);
        binding.statusText.setVisibility(View.VISIBLE);
        disableSendButton();
    }

    @Override
    public void onEmailInserted() {
        enableSendButton();
    }

    @Override
    public void onEmailEmpty() {
        disableSendButton();
    }

    @Override
    public void onError() {
        binding.statusText.setText(R.string.unknown_error);
        binding.statusText.setVisibility(View.VISIBLE);
        disableSendButton();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideKeyBoard();
        binding.email.setText("");
        binding.statusText.setText("");
        enableSendButton();
    }

    private void hideKeyBoard(){
        KeyboardHelper.hideKeyboard(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
    }
}
