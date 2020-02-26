package upuphere.com.upuphere.ui.onboarding;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.helper.PrefManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener{
    View rootView;


    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        return rootView;
    }

    PrefManager prefManager;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        view.findViewById(R.id.termOfUse).setOnClickListener(this);
        view.findViewById(R.id.privacyPolicy).setOnClickListener(this);
        view.findViewById(R.id.agreeContinue).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);
        switch (view.getId()){
            case R.id.termOfUse:
                NavDirections navDirections = WelcomeFragmentDirections.actionWelcomeFragmentToAgreementFragment(AgreementFragment.TERM_OF_USE);
                navController.navigate(navDirections);
                break;
            case R.id.privacyPolicy:
                NavDirections navDirections1 = WelcomeFragmentDirections.actionWelcomeFragmentToAgreementFragment(AgreementFragment.PRIVACY_POLICY);
                navController.navigate(navDirections1);
                break;
            case R.id.agreeContinue:
                prefManager.setIsUserAgreedTerm(true);
                navController.popBackStack(R.id.welcomeFragment,true);
                navController.navigate(R.id.mainFragment);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
    }
}
