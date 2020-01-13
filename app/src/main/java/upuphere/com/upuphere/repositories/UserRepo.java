package upuphere.com.upuphere.repositories;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import upuphere.com.upuphere.Interface.AuthListener;
import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.VolleyRequest;
import upuphere.com.upuphere.models.UserModel;

public class UserRepo{

    private static UserRepo instance;
    private ArrayList<UserModel> dataSet = new ArrayList<>();

    public static UserRepo getInstance(){
        if(instance == null){
            instance = new UserRepo();
        }
        return instance;
    }



    public void loginUser(String identity, String password, final AuthListener authListener){
        Log.d("Login called","called");
        String[] keys = new String[]{"identity","password","device_type"};
        String[] values = new String[]{"abcd","geh", AppConfig.PLATFORM};

        JSONObject params = VolleyRequest.getParams(keys,values);

        JsonObjectRequest request = VolleyRequest.postJsonAccessRequestWithoutRetry(AppConfig.URL_LOGIN,params, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {

                Gson gson = new Gson();
                UserModel user = gson.fromJson(response.toString(),UserModel.class);

                PrefManager prefManager = new PrefManager(AppController.getContext());
                prefManager.setUserID(user.getUserId());
                prefManager.setUserSessionId(user.getUserSessionId());
                prefManager.setUserDeviceId(user.getUserDeviceId());
                prefManager.setUserAccessToken(user.getAccessToken());
                prefManager.setUserRefreshToken(user.getRefreshToken());

                prefManager.setSharedPreferences(PrefManager.IS_LOGGED_IN,true);

                authListener.onSuccess();
            }

            @Override
            public void onError(String error) {
                authListener.onFailure(error);
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

    public void signUp(String phoneNumber, String email, String password, String username, final CommonCallBack callBack){

        String[] keys = new String[]{"email","username","password","phone_number","device_type"};
        String[] values = new String[]{"abcdsasdd2","eaddsasdfc3","geaasds4h","asaasdsdwer45d",AppConfig.PLATFORM};
        JSONObject params = VolleyRequest.getParams(keys,values);

        JsonObjectRequest jsonReq = VolleyRequest.postJsonAccessRequestWithoutRetry(AppConfig.URL_SIGN_UP, params, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {

                Gson gson = new Gson();
                UserModel user = gson.fromJson(response.toString(),UserModel.class);
                Log.d("User",user.toString());

                PrefManager prefManager = new PrefManager(AppController.getContext());
                prefManager.setUserID(user.getUserId());
                prefManager.setUserSessionId(user.getUserSessionId());
                prefManager.setUserDeviceId(user.getUserDeviceId());
                prefManager.setUserAccessToken(user.getAccessToken());
                prefManager.setUserRefreshToken(user.getRefreshToken());

                prefManager.setSharedPreferences(PrefManager.IS_LOGGED_IN,true);

                callBack.success();
            }

            @Override
            public void onError(String error) {
                callBack.showError(error);

            }
        });
        jsonReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);

    }

    /**
     * DetailsTypes
     * 222 == Check Username and Email
     * 333 == Check Phone Number
     * */
    public void checkDetailsExisted(String credentials, int detailsTypes, final BoolCallBack callBack){

        String field = "";
        switch (detailsTypes){
            case 222:
                field = "identity";
                break;
            case 333:
                field = "phone_number";
                break;
        }

        String[] key = new String[]{field};
        String[] values = new String[]{credentials};
        JSONObject jsonObject = VolleyRequest.getParams(key,values);

        Log.d("Json object",jsonObject.toString());

        JsonObjectRequest jsonReq = VolleyRequest.postJsonAccessRequestWithoutRetry(AppConfig.URL_COMPARE_DETAILS, jsonObject, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean isExisted = response.getBoolean("is_existed");
                    callBack.success(isExisted);

                    Log.d("Existed",String.valueOf(isExisted));

                }catch (Exception e){
                    e.printStackTrace();
                    callBack.showError("Error Occurred");
                }
            }

            @Override
            public void onError(String error) {
                callBack.showError(error);
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);

    }

    public void revokedAccessToken(final CommonCallBack callBack){
        PrefManager prefManager = new PrefManager(AppController.getContext());
        JSONObject jsonObject = VolleyRequest.getParams(new String[]{"session_id","device_id"},new String[]{prefManager.getUserSessionId(),prefManager.getUserDeviceId()});

        JsonObjectRequest request = VolleyRequest.postJsonAccessRequestWithoutRetry(AppConfig.URL_LOGOUT_ACCESS_TOKEN, jsonObject, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("response",response.toString());
                callBack.success();
            }

            @Override
            public void onError(String error) {
                callBack.showError(error);
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    public void revokeRefreshToken(final CommonCallBack callBack){

        JsonObjectRequest request = VolleyRequest.postJsonRefreshRequestWithoutRetry(AppConfig.URL_LOGOUT_REFRESH_TOKEN, null, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                callBack.success();
            }

            @Override
            public void onError(String error) {
                callBack.showError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(request);

    }
}
