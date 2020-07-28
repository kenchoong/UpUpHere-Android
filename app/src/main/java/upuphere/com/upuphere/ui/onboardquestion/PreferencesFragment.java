package upuphere.com.upuphere.ui.onboardquestion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentDobBinding;
import upuphere.com.upuphere.databinding.FragmentPreferencesBinding;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PreferencesFragment extends Fragment implements OnBoardingViewModel.OnBoardingInterface {


    public PreferencesFragment() {
        // Required empty public constructor
    }

    View rootView;
    OnBoardingViewModel viewModel;
    FragmentPreferencesBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preferences, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(OnBoardingViewModel.class);
        rootView = binding.getRoot();
        binding.setViewModel(viewModel);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.setOnBoardingInterface(this);
    }

    @Override
    public void onGenderNext() {

    }

    @Override
    public void onDobNext() {

    }

    @Override
    public void onPreferencesNext() {
        NavController navController = Navigation.findNavController(rootView);
        navController.popBackStack(R.id.preferencesFragment,true);
        navController.navigate(R.id.mainFragment);
    }
}