package upuphere.com.upuphere.libs;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.helper.VolleyRequest;

public class Authenticate {
    private static final String TAG = "API_HELPER";


    public static void login(final Context mContext, final String identityString, final String password, final CommonCallBack callBack){

        String[] keys = new String[]{"identity","password","device_type"};
        String[] values = new String[]{"abcd","geh",AppConfig.PLATFORM};

        JSONObject params = VolleyRequest.getParams(keys,values);
        JsonObjectRequest jsonReq = VolleyRequest.postJsonAccessRequestWithoutRetry(mContext, AppConfig.URL_LOGIN, params, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String userId = response.getString("user_id");
                    String userSessionId = response.getString("user_session_id");
                    String userDeviceId = response.getString("user_device_id");
                    String access_token = response.getString("access_token");
                    String refresh_token = response.getString("refresh_token");

                    Log.d("access",access_token);
                    Log.d("refresh",refresh_token);

                    PrefManager prefManager = new PrefManager(mContext);
                    prefManager.setUserID(userId);
                    prefManager.setUserDeviceId(userDeviceId);
                    prefManager.setUserSessionId(userSessionId);
                    prefManager.setUserAccessToken(access_token);
                    prefManager.setUserRefreshToken(refresh_token);

                    callBack.success();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                //parseVolleyError(error);
                callBack.showError(error);
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    /**
     * DetailsTypes
     * 222 == Check Username and Email
     * 333 == Check Phone Number
     * */
    public static void checkDetailsExisted(Context mContext, String credentials, int detailsTypes, final BoolCallBack callBack){

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

        JsonObjectRequest jsonReq = VolleyRequest.postJsonAccessRequestWithoutRetry(mContext, AppConfig.URL_COMPARE_DETAILS, jsonObject, new VolleyRequest.ResponseCallBack() {
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

    public static void signUp(final Context mContext, String phoneNumber, String email, String password, String username, final CommonCallBack callBack){

        String[] keys = new String[]{"email","username","password","phone_number","device_type"};
        String[] values = new String[]{"abcd","efc","geh","aswerd",AppConfig.PLATFORM};
        JSONObject params = VolleyRequest.getParams(keys,values);

        JsonObjectRequest jsonReq = VolleyRequest.postJsonAccessRequestWithoutRetry(mContext, AppConfig.URL_SIGN_UP, params, new VolleyRequest.ResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                try {

                    String user_id = response.getString("user_id");
                    String user_session_id = response.getString("user_session_id");
                    String user_device_id = response.getString("user_device_id");
                    String access_token = response.getString("access_token");
                    String refresh_token = response.getString("refresh_token");

                    Log.d("access",access_token);
                    Log.d("refresh",refresh_token);

                    PrefManager prefManager = new PrefManager(mContext);
                    prefManager.setUserID(user_id);
                    prefManager.setUserSessionId(user_session_id);
                    prefManager.setUserDeviceId(user_device_id);
                    prefManager.setUserAccessToken(access_token);
                    prefManager.setUserRefreshToken(refresh_token);
                    callBack.success();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    public static void revokedAccessToken(Context mContext, final CommonCallBack callBack){
        PrefManager prefManager = new PrefManager(mContext);
        JSONObject jsonObject = VolleyRequest.getParams(new String[]{"session_id","device_id"},new String[]{prefManager.getUserSessionId(),prefManager.getUserDeviceId()});

        JsonObjectRequest request = VolleyRequest.postJsonAccessRequestWithoutRetry(mContext, AppConfig.URL_LOGOUT_ACCESS_TOKEN, jsonObject, new VolleyRequest.ResponseCallBack() {
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

    public static void revokeRefreshToken(Context mContext, final CommonCallBack callBack){

        JsonObjectRequest request = VolleyRequest.postJsonRefreshRequestWithoutRetry(mContext, AppConfig.URL_LOGOUT_REFRESH_TOKEN, null, new VolleyRequest.ResponseCallBack() {
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
