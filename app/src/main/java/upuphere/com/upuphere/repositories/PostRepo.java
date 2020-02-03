package upuphere.com.upuphere.repositories;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.VolleyMultipartRequest;
import upuphere.com.upuphere.helper.VolleyRequest;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;

public class PostRepo {

    private MutableLiveData<List<CommentModel>> commentMutableLiveData = new MutableLiveData<>();

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
