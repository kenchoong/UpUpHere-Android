package upuphere.com.upuphere.repositories;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.VolleyMultipartRequest;
import upuphere.com.upuphere.helper.VolleyRequest;

public class PostRepo {

    private static PostRepo instance;

    public static PostRepo getInstance(){
        if(instance == null){
            instance = new PostRepo();
        }
        return instance;
    }

    public void createPost(String roomId, String statusText, Bitmap bitmap,String mediaType, final StringCallBack callBack){
        String fileKey = "post_file";

        String[] key = new String[]{"room_id","post_text","media_type"};
        String[] values = new String[]{roomId,statusText,mediaType};
        JSONObject params = VolleyRequest.getParams(key,values);
        VolleyMultipartRequest request = VolleyRequest.postMultipartAccessRequest(AppConfig.URL_CREATE_POST_TO_ROOM, params, bitmap,fileKey, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String postId = response.getString("post_id");

                    Log.d("POST REPO ID",postId);

                    callBack.success(postId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("POST REPO ERROR",error);
                callBack.showError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }
}
