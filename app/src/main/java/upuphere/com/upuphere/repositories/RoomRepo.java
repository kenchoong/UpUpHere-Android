package upuphere.com.upuphere.repositories;

import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.MutableLiveData;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.VolleyMultipartRequest;
import upuphere.com.upuphere.helper.VolleyRequest;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.models.PostModel;
import upuphere.com.upuphere.models.RoomModel;
import upuphere.com.upuphere.models.UserModel;

public class RoomRepo {

    private static RoomRepo instance;
    private MutableLiveData<List<AllRooms>> roomMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Post>> postMutableLiveData = new MutableLiveData<>();

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

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void createRoom(String roomName, final Bitmap bitmap, final StringCallBack stringCallBack) {

        String[] key = new String[]{"room_name"};
        String[] values = new String[]{roomName};
        final JSONObject param = VolleyRequest.getParams(key, values);
        Log.d("CREATE ROOM",param.toString());

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, AppConfig.URL_CREATE_ROOM, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    JSONObject obj = new JSONObject(new String(response.data));
                    Log.d("CREATE ROOM",obj.toString());
                    stringCallBack.success(obj.getString("room_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CREATE ROOM","ERROR OCCURRED ");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("data", param.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("room_image_file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyRequest.volleyAccessClient();
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

}
