package upuphere.com.upuphere.repositories;

import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.VolleyRequest;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.RoomModel;
import upuphere.com.upuphere.models.UserModel;

public class RoomRepo {

    private static RoomRepo instance;
    private ArrayList<RoomRepo> dataSet = new ArrayList<>();

    private ArrayList<AllRooms> newRooms = new ArrayList<>();
    private MutableLiveData<List<AllRooms>> roomMutableLiveData = new MutableLiveData<>();

    public static RoomRepo getInstance(){
        if(instance == null){
            instance = new RoomRepo();
        }
        return instance;
    }

    public MutableLiveData<List<AllRooms>> getRoomListMutableData(){
        JsonObjectRequest request = VolleyRequest.getJsonAccessRequestWithoutRetry(AppConfig.URL_GET_ROOM_LIST, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {

                Gson gson = new Gson();
                RoomModel room = gson.fromJson(response.toString(),RoomModel.class);
                roomMutableLiveData.setValue(room.getAllRooms());
            }

            @Override
            public void onError(String error) {
                Log.d("GET ROOM ERROR",error);
            }
        });

        AppController.getInstance().addToRequestQueue(request);

        return roomMutableLiveData;
    }

}
