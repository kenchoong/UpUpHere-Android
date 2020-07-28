package upuphere.com.upuphere.ui.onboardquestion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import upuphere.com.upuphere.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class OnBoardingFragment extends Fragment implements View.OnClickListener {

    public OnBoardingFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView.findViewById(R.id.continueButton).setOnClickListener(this);
        rootView.findViewById(R.id.skipButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NavController navController = Navigation.findNavController(rootView);
        switch (v.getId()){
            case R.id.continueButton:
                navController.popBackStack(R.id.onBoardingFragment,true);
                navController.navigate(R.id.genderFragment);
                break;
            case R.id.skipButton:
                navController.popBackStack(R.id.onBoardingFragment,true);
                navController.navigate(R.id.mainFragment);
                break;
        }
    }
}