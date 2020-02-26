package upuphere.com.upuphere.ui.onboarding;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import upuphere.com.upuphere.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgreementFragment extends Fragment {


    public static final int PRIVACY_POLICY = 1111;
    public static final int TERM_OF_USE = 2222;

    public AgreementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        int agreementType = AgreementFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getAgreementType();

        if(agreementType == PRIVACY_POLICY){
            Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.privacy_policy);
        }
        else if(agreementType == TERM_OF_USE){
            Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.term_of_use);
        }

        return inflater.inflate(R.layout.fragment_agreement, container, false);
    }

}
