package upuphere.com.upuphere.ui.room;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import upuphere.com.upuphere.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayRoomFragment extends Fragment {


    public DisplayRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String roomId = DisplayRoomFragmentArgs.fromBundle(getArguments()).getRoomId();
        Log.d("ROOM ID DISPLAY",roomId);

        return inflater.inflate(R.layout.fragment_display_room, container, false);
    }

}
