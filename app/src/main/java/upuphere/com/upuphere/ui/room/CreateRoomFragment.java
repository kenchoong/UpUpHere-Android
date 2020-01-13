package upuphere.com.upuphere.ui.room;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import upuphere.com.upuphere.R;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.models.UserModel;


public class CreateRoomFragment extends Fragment {

    PrefManager prefManager;

    public CreateRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        prefManager = new PrefManager(getActivity());

        Gson gson = new Gson();
        String userObjectString = prefManager.getUserDetailsObject();
        UserModel user = gson.fromJson(userObjectString,UserModel.class);
        Log.d("MAIN USER ID", user.getUserId());
        Log.d("MAIN IS LOGGED IN",String.valueOf(prefManager.isLoggedIn()));
        return inflater.inflate(R.layout.fragment_create_room, container, false);
    }

}
