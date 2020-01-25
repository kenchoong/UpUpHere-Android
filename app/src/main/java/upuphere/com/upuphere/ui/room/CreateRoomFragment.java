package upuphere.com.upuphere.ui.room;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentCreateRoomBinding;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.models.UserModel;
import upuphere.com.upuphere.viewmodel.CreateRoomViewModel;
import upuphere.com.upuphere.viewmodel.MainViewModel;


public class CreateRoomFragment extends Fragment {

    PrefManager prefManager;

    public CreateRoomFragment() {
        // Required empty public constructor
    }

    CreateRoomViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        prefManager = new PrefManager(getActivity());
        //return inflater.inflate(R.layout.fragment_create_room, container, false);
        FragmentCreateRoomBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_room,container,false);
        viewModel = ViewModelProviders.of(requireActivity()).get(CreateRoomViewModel.class);
        View view = binding.getRoot();
        binding.setViewmodel(viewModel);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);
        viewModel.createRoomState.observe(getViewLifecycleOwner(), new Observer<CreateRoomViewModel.CreateRoomState>() {
            @Override
            public void onChanged(CreateRoomViewModel.CreateRoomState createRoomState) {
                switch (createRoomState){
                    case EXIT_CREATE_ROOM_PROCESS:
                        navController.navigateUp();
                        break;
                }
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        //todo:: when click back,back to main fragment
                        viewModel.exitCreateRoom();


                    }
                });

    }
}
