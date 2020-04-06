package upuphere.com.upuphere.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.adapter.NotificationAdapter;
import upuphere.com.upuphere.databinding.FragmentNotificationBinding;
import upuphere.com.upuphere.models.NotificationModel;
import upuphere.com.upuphere.viewmodel.NotificationViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements NotificationAdapter.NotificationAdapterListener {
    private static final String TAG = NotificationFragment.class.getName();

    FragmentNotificationBinding binding;
    NotificationViewModel viewModel;
    NotificationAdapter adapter;
    RecyclerView mRecyclerView;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_notification, container, false);

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_notification,container,false);
        viewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        rootView = binding.getRoot();
        binding.setViewmodel(viewModel);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        getNotificationFromLocal();
    }

    private void setupRecyclerView() {
        mRecyclerView = binding.notificationRecyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        adapter = new NotificationAdapter(this);
        mRecyclerView.setAdapter(adapter);
    }

    private void getNotificationFromLocal() {
        viewModel.getNotificationList().observe(getViewLifecycleOwner(), new Observer<List<NotificationModel>>() {
            @Override
            public void onChanged(List<NotificationModel> notificationModels) {
                if(notificationModels != null && notificationModels.size()> 0){
                    binding.notificationRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyStateNotification.setVisibility(View.GONE);
                    adapter.setNotificationList(notificationModels);
                }else {
                    binding.notificationRecyclerView.setVisibility(View.GONE);
                    binding.emptyStateNotification.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onNotificationClicked(NotificationModel notification) {
        Log.d(TAG, "Notification ID = " + notification.getId());

        viewModel.deleteNotification(notification);

        NavDirections action = NotificationFragmentDirections.actionNotificationFragmentToSinglePostFragment(notification.getPostId());
        Navigation.findNavController(rootView).navigate(action);
    }
}

