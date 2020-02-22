package upuphere.com.upuphere.repositories;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.MutableLiveData;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.database.PostDao;
import upuphere.com.upuphere.database.UpUpHereDatabase;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.VolleyMultipartRequest;
import upuphere.com.upuphere.helper.VolleyRequest;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.models.PostModel;

public class PostRepo {

    private MutableLiveData<List<CommentModel>> commentMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Post>> postMutableLiveData = new MutableLiveData<>();

    private PostDao postDao;

    public PostRepo(Application application){
        UpUpHereDatabase db = UpUpHereDatabase.getDatabase(application);
        postDao = db.postDao();
    }

    public void createPost(String roomId, String statusText, Bitmap bitmap,String mediaType, final StringCallBack callBack){
        if(bitmap != null) {
            String fileKey = "post_file";

            String[] key = new String[]{"room_id", "post_text", "media_type"};
            String[] values = new String[]{roomId, statusText, mediaType};
            JSONObject params = VolleyRequest.getParams(key, values);
            VolleyMultipartRequest request = VolleyRequest.postMultipartAccessRequest(AppConfig.URL_CREATE_POST_TO_ROOM, params, bitmap, fileKey, new VolleyRequest.ResponseCallBack() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        String postId = response.getString("post_id");

                        Log.d("POST REPO ID", postId);

                        callBack.success(postId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.d("POST REPO ERROR", error);
                    callBack.showError(error);
                }
            });

            AppController.getInstance().addToRequestQueue(request);
        }else{
            String[] k = new String[]{"room_id","post_text"};
            String[] v= new String[]{roomId,statusText};
            final JSONObject contentObject = VolleyRequest.getParams(k,v);

            String[] key = new String[]{"data"};
            String[] value = new String[]{contentObject.toString()};
            Map<String,String> params = VolleyRequest.getStringParams(key,value);

            StringRequest request = VolleyRequest.postFormDataStringRequestWithAccessToken(AppConfig.URL_CREATE_POST_TO_ROOM,params, new StringCallBack() {
                @Override
                public void success(String response) {
                    Log.d("POST REQUEST", response);

                    try {
                        JSONObject json = new JSONObject(response);
                        String postId = json.getString("post_id");

                        Log.d("POST REPO ID", postId);
                        callBack.success(postId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void showError(String error) {
                    Log.d("POST REQUEST",error);
                    callBack.showError(error);
                }
            });

            AppController.getInstance().addToRequestQueue(request);
        }
    }

    public MutableLiveData<List<Post>> getSinglePostByPostId(final String postId, final StringCallBack callback){

        if(AppController.getInstance().internetConnectionAvailable()){
            PrefManager prefManager = new PrefManager(AppController.getContext());
            String userId = prefManager.getUserId();

            String url = "";
            if(userId != null){
                url = AppConfig.URL_GET_SINGLE_POST + postId + "?user_public_id=" + userId;
            }else{
                url = AppConfig.URL_GET_SINGLE_POST + postId;
            }

            JsonObjectRequest request = VolleyRequest.getJsonAccessRequestWithoutRetry(url, new VolleyRequest.ResponseCallBack() {
                @Override
                public void onSuccess(JSONObject response) {
                    if(!response.has("error")){
                        Gson gson = new Gson();
                        PostModel postModel = gson.fromJson(response.toString(),PostModel.class);

                        postMutableLiveData.setValue(postModel.getSinglePost());
                    }else{
                        try {
                            String message = response.getString("message");
                            callback.showError(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    getPostByPostIdFromLocalDb(postId);
                }
            });

            AppController.getInstance().addToRequestQueue(request);
        }else{
            getPostByPostIdFromLocalDb(postId);
        }

        return postMutableLiveData;
    }

    public void unHidePost(String postId, final StringCallBack callBack){
        String[] key = new String[]{"blocked_post_id"};
        String[] value = new String[]{postId};
        JSONObject params = VolleyRequest.getParams(key,value);
        JsonObjectRequest request = VolleyRequest.putJsonRequestWithAccessToken(AppConfig.URL_BLOCK_UNBLOCK, params, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String message = response.getString("message");
                    callBack.success(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBack.showError(e.getMessage());
                }
            }
            @Override
            public void onError(String error) {
                callBack.showError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }


    public MutableLiveData<List<Post>> getAllPostWithRoomId(final String roomId){

        if(AppController.getInstance().internetConnectionAvailable()){

            PrefManager prefManager = new PrefManager(AppController.getContext());
            String userId = prefManager.getUserId();

            String url;
            if(userId != null){
                url = AppConfig.URL_GET_POST_IN_SPECIFIC_ROOM + roomId + "?user_public_id=" + userId;
            }else{
                url = AppConfig.URL_GET_POST_IN_SPECIFIC_ROOM + roomId;
            }

            JsonObjectRequest request = VolleyRequest.getJsonAccessRequestWithoutRetry(url, new VolleyRequest.ResponseCallBack() {
                @Override
                public void onSuccess(JSONObject response) {

                    Gson gson = new Gson();
                    PostModel postModel = gson.fromJson(response.toString(), PostModel.class);

                    insertNewFetchPostOfARoomIntoLocalDb(postModel.getPost());
                    postMutableLiveData.setValue(postModel.getPost());
                }

                @Override
                public void onError(String error) {
                    Log.d("GET POST IN ROOM",error);
                    getPostByRoomIdFromLocalDb(roomId);
                }
            });
            AppController.getInstance().addToRequestQueue(request);


        }else{
            getPostByRoomIdFromLocalDb(roomId);
        }

        return postMutableLiveData;
    }

    public void setPostMutableLiveDataToNull(){
        List<Post> postList = new ArrayList<>();
        postMutableLiveData.setValue(postList);
    }

    private void getPostByRoomIdFromLocalDb(final String roomId){
        UpUpHereDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("POST DAO", String.valueOf(postDao.getPostByRoomIdInLocalDb(roomId).size()));
                postMutableLiveData.postValue(postDao.getPostByRoomIdInLocalDb(roomId));
            }
        });
    }

    private void getPostByPostIdFromLocalDb(final String postId){
        UpUpHereDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                postMutableLiveData.postValue(postDao.getPostByPostIdInLocalDb(postId));
            }
        });
    }

    private void insertNewFetchPostOfARoomIntoLocalDb(final List<Post> postListInRoomFromNetwork){
        UpUpHereDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                postDao.deleteAll();
                postDao.insert(postListInRoomFromNetwork);
            }
        });
    }

    public MutableLiveData<List<CommentModel>> getCommentMutableLiveData(List<Post> posts, String postId){

        for(Post post: posts){
            if(post.getId().equals(postId)){
                commentMutableLiveData.setValue(post.getComment());
            }
        }

        return commentMutableLiveData;
    }

    public void appenNewCommentToMutableLiveData(List<Post>posts,String postId,CommentModel comment){
        for(Post post: posts){
            if(post.getId().equals(postId)){
                post.getComment().add(comment);
                commentMutableLiveData.setValue(post.getComment());
            }
        }
    }

    public void createComment(String postId, String commentText, final StringCallBack callBack){

        String[] keys = new String[]{"post_id","comment_text"};
        String[] values = new String[]{postId,commentText};
        JSONObject params = VolleyRequest.getParams(keys,values);

        JsonObjectRequest request = VolleyRequest.postJsonAccessRequestWithoutRetry(AppConfig.URL_CREATE_COMMENT, params, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                callBack.success("Success");
            }

            @Override
            public void onError(String error) {
                callBack.showError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }
}
