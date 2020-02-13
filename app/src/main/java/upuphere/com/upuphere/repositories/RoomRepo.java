package upuphere.com.upuphere.repositories;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import androidx.lifecycle.MutableLiveData;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.database.RoomDao;
import upuphere.com.upuphere.database.UpUpHereDatabase;
import upuphere.com.upuphere.helper.VolleyMultipartRequest;
import upuphere.com.upuphere.helper.VolleyRequest;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.models.PostModel;
import upuphere.com.upuphere.models.RoomModel;

public class RoomRepo {

    private static RoomRepo instance;
    private MutableLiveData<List<AllRooms>> roomMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Post>> postMutableLiveData = new MutableLiveData<>();
    private RoomDao roomDao;
    private List<AllRooms> roomsListFromLocalDb = new ArrayList<>();


    public RoomRepo(Application application){
        UpUpHereDatabase db = UpUpHereDatabase.getDatabase(application);
        roomDao = db.roomDao();
    }

    public MutableLiveData<List<AllRooms>> getRoomListMutableData(){

        if(AppController.getInstance().internetConnectionAvailable()){
            JsonObjectRequest request = VolleyRequest.getJsonAccessRequestWithoutRetry(AppConfig.URL_GET_ROOM_LIST, new VolleyRequest.ResponseCallBack() {
                @Override
                public void onSuccess(JSONObject response) {

                    Gson gson = new Gson();
                    RoomModel room = gson.fromJson(response.toString(),RoomModel.class);
                    //todo:: here need to remove the data 1st then only insert
                    insertNewFetchRoomIntoLocalDb(room.getAllRooms());
                    roomMutableLiveData.setValue(room.getAllRooms());
                }

                @Override
                public void onError(String error) {
                    Log.d("GET ROOM ERROR",error);
                    roomMutableLiveData.setValue(getAllRoomFromLocalDB());
                }
            });

            AppController.getInstance().addToRequestQueue(request);
        }else{
            roomMutableLiveData.setValue(getAllRoomFromLocalDB());
        }

        return roomMutableLiveData;
    }


    private List<AllRooms> getAllRoomFromLocalDB(){

        UpUpHereDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("ROOM DAO", String.valueOf(roomDao.getAllRoomInLocalDb().size()));
                roomsListFromLocalDb.clear();
                roomsListFromLocalDb.addAll(roomDao.getAllRoomInLocalDb());

            }
        });

        return roomsListFromLocalDb;
    }

    private void insertNewFetchRoomIntoLocalDb(final List<AllRooms> roomList){
        UpUpHereDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                roomDao.deleteAll();
                roomDao.insert(roomList);
            }
        });
    }

    public MutableLiveData<List<Post>> getAllPostWithRoomId(String roomId){
        String url = AppConfig.URL_GET_POST_IN_SPECIFIC_ROOM + roomId;

        JsonObjectRequest request = VolleyRequest.getJsonAccessRequestWithoutRetry(url, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {

                Gson gson = new Gson();
                PostModel postModel = gson.fromJson(response.toString(), PostModel.class);
                postMutableLiveData.setValue(postModel.getPost());
            }

            @Override
            public void onError(String error) {
                Log.d("GET POST IN ROOM",error);
            }
        });

        AppController.getInstance().addToRequestQueue(request);

        return postMutableLiveData;
    }


    public void createRoom(String roomName, final Bitmap bitmap, final StringCallBack stringCallBack) {
        String fileKey = "room_image_file";

        String[] key = new String[]{"room_name"};
        String[] values = new String[]{roomName};
        JSONObject params = VolleyRequest.getParams(key,values);
        VolleyMultipartRequest request = VolleyRequest.postMultipartAccessRequest(AppConfig.URL_CREATE_ROOM, params, bitmap,fileKey, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    stringCallBack.success(response.getString("room_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(String error) {
                stringCallBack.showError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

}
