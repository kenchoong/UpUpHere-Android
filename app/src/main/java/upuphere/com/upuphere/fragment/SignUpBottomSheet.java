package upuphere.com.upuphere.fragment;

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import upuphere.com.upuphere.R;
import upuphere.com.upuphere.helper.SpannableStringHelper;
import upuphere.com.upuphere.ui.onboarding.AgreementFragment;

public class SignUpBottomSheet extends BottomSheetDialogFragment {

    private void makeLinks() {
        List<String> stringToClick = new ArrayList<>();
        stringToClick.add(getResources().getString(R.string.term_of_use));
        stringToClick.add(getResources().getString(R.string.privacy_policy));

        ClickableSpan goToTerms = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                onSignUpSheetInterface.onTermClicked();
            }
        };

        ClickableSpan goToPrivacyPolicy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                onSignUpSheetInterface.onPrivacyClicked();
            }
        };

        List<ClickableSpan>clickableActions = new ArrayList<>();
        clickableActions.add(goToTerms);
        clickableActions.add(goToPrivacyPolicy);

        SpannableStringHelper.makeLinks(termAndPrivacyPolicy, getResources().getString(R.string.term_and_policy),  stringToClick, clickableActions);
    }

    private void createSpannableTextViewAndButton() {
        List<String> stringToClick = new ArrayList<>();
        stringToClick.add(getResources().getString(R.string.login));

        ClickableSpan goToLogin = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                onSignUpSheetInterface.onLoginClicked();
            }
        };

        List<ClickableSpan>clickableActions = new ArrayList<>();
        clickableActions.add(goToLogin);

        SpannableStringHelper.makeLinks(redirectLoginText, getResources().getString(R.string.redirect_sign_in), stringToClick, clickableActions);
    }

    public static final String TAG = "Sign up fragment";

    public static SignUpBottomSheet newInstance(){
        return new SignUpBottomSheet();
    }

    private OnSignUpSheetInterface onSignUpSheetInterface;
    public interface OnSignUpSheetInterface{
        void onCreateButtonClicked();
        void onTermClicked();
        void onLoginClicked();
        void onPrivacyClicked();
    }

    public void setOnSignUpSheetInterface(OnSignUpSheetInterface onSignUpSheetInterface){
        this.onSignUpSheetInterface = onSignUpSheetInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_bottom_sheet,container,false);
    }

    TextView redirectLoginText;
    TextView termAndPrivacyPolicy;
    Button signUpButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        redirectLoginText = view.findViewById(R.id.redirect_login_text);
        termAndPrivacyPolicy = view.findViewById(R.id.termAndPrivacyPolicy);
        signUpButton = view.findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpSheetInterface.onCreateButtonClicked();
            }
        });

        makeLinks();
        createSpannableTextViewAndButton();
    }
}
