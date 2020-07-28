package upuphere.com.upuphere.ui.onboardquestion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentGenderBinding;

// Instances of this class are fragments representing a single
// object in our collection.
public class GenderFragment extends Fragment implements OnBoardingViewModel.OnBoardingInterface{

    View rootView;
    OnBoardingViewModel viewModel;
    FragmentGenderBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gender, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(OnBoardingViewModel.class);
        rootView = binding.getRoot();
        binding.setViewModel(viewModel);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel.setOnBoardingInterface(this);
    }

    @Override
    public void onGenderNext() {
        NavController navController = Navigation.findNavController(rootView);
        navController.popBackStack(R.id.genderFragment,true);
        navController.navigate(R.id.dobFragment);
    }

    @Override
    public void onDobNext() {

    }

    @Override
    public void onPreferencesNext() {

    }
}