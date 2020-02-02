package upuphere.com.upuphere.ui.room;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.MainActivity;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.adapter.PostAdapter;
import upuphere.com.upuphere.databinding.FragmentDisplayRoomBinding;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.viewmodel.DisplayRoomViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayRoomFragment extends Fragment implements PostAdapter.PostAdapterListener{


    public DisplayRoomFragment() {
        // Required empty public constructor
    }

    private View rootView;
    private DisplayRoomViewModel viewModel;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    public String roomId;
    private FragmentDisplayRoomBinding binding;
    String roomName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AllRooms room = DisplayRoomFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getRoom();
        roomId = room.getId();
        roomName = room.getRoomName();

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(roomName);

        Log.d("ROOM NAME",roomName);
        Log.d("ROOM ID DISPLAY",roomId);

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_display_room,container,false);
        viewModel = ViewModelProviders.of(requireActivity()).get(DisplayRoomViewModel.class);
        rootView = binding.getRoot();
        binding.setModel(viewModel);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.setDisplayRoomInterface(new DisplayRoomViewModel.DisplayRoomInterface() {
            @Override
            public void onFabClick() {
                NavDirections action = DisplayRoomFragmentDirections.actionRoomFragmentToCreatePostFragment(roomId);
                Navigation.findNavController(view).navigate(action);
            }
        });

        initializeRecyclerView();

        getPostInRoom();
    }

    private void initializeRecyclerView() {
        recyclerView = binding.postRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        postAdapter = new PostAdapter(this);
        recyclerView.setAdapter(postAdapter);
    }

    private void getPostInRoom() {
        viewModel.getAllPostInRoom(roomId).observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                postAdapter.setPost(posts);
            }
        });
    }

    @Override
    public void onCommentClicked(Post post) {
        Toast.makeText(getActivity(),post.getId(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareClicked(Post post) {
        Toast.makeText(getActivity(), post.getAuthor(), Toast.LENGTH_SHORT).show();
    }
}
