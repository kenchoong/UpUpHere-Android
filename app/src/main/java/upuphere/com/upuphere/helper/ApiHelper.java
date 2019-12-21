package upuphere.com.upuphere.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import upuphere.com.upuphere.Interface.BoolCallBack;
import upuphere.com.upuphere.Interface.CommonCallBack;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;

public class ApiHelper {
    private static final String TAG = "API_HELPER";

    public void loginUser(final Context mContext, final String identityString, final String password, final CommonCallBack callBack){
        Map<String,String> params = new HashMap<>();
        params.put("identity",identityString);
        params.put("password",password);
        params.put("device_type",AppConfig.PLATFORM);
        JSONObject jsonObject = new JSONObject(params);

        // making fresh volley request and getting json
       JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.URL_LOGIN , jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Login Response",response.toString());
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
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.showError("Some Error Occurred");
                parseVolleyError(error);
            }
        }){

           @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String,String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");

                return headers;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    /**
     * DetailsTypes
     * 222 == Check Username and Email
     * 333 == Check Phone Number
     * */
    public void checkDetailsExisted(String credentials,int detailsTypes,final BoolCallBack callBack){

        String key = "";
        switch (detailsTypes){
            case 222:
                key = "identity";
                break;
            case 333:
                key = "phone_number";
                break;
        }

        Map<String,String> params = new HashMap<>();
        params.put(key,credentials);
        JSONObject jsonObject = new JSONObject(params);

        Log.d("Json object",jsonObject.toString());
        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.URL_COMPARE_DETAILS , jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean isExisted = response.getBoolean("is_existed");
                    callBack.success(isExisted);

                    Log.d("Existed",String.valueOf(isExisted));

                }catch (Exception e){
                    e.printStackTrace();
                    callBack.showError("Error Occurred");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                parseVolleyError(error);
                callBack.showError("Error Occurred");
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String,String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");

                return headers;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);

    }

    public void signUpUser(final Context mContext,String phoneNumber,String email,String password,String username,final CommonCallBack callBack){

        Map<String,String> params = new HashMap<>();
        params.put("email",email);
        params.put("username",username);
        params.put("password",password);
        params.put("phone_number",phoneNumber);
        params.put("device_type",AppConfig.PLATFORM);
        JSONObject jsonObject = new JSONObject(params);

        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.URL_SIGN_UP , jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Sign Up Response",response.toString());
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
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.showError("Some Error Occurred");
                parseVolleyError(error);
                Log.d(TAG,"OnErrorResponse" + error);
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String,String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");

                return headers;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    public void revokedAccessToken(Context mContext,final String accessToken, final CommonCallBack callBack){
        PrefManager prefManager = new PrefManager(mContext);
        Map<String,String> params = new HashMap<>();
        params.put("session_id",prefManager.getUserSessionId());
        params.put("device_id",prefManager.getUserDeviceId());
        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_LOGOUT_ACCESS_TOKEN, jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("onResponse", response.toString());
                callBack.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    public void revokeRefreshToken(Context mContext, final String refreshToken, final CommonCallBack callBack){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_LOGOUT_REFRESH_TOKEN, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("onResponse", response.toString());
                callBack.success();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", "Bearer " + refreshToken);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(request);

    }

    private void parseVolleyError(VolleyError error) {
        try {
            Log.d("Json error",error.toString());
            if(error.networkResponse != null){
                int statusCode = error.networkResponse.statusCode;

                String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject data = new JSONObject(responseBody);
                String message = data.optString("message");

                if (statusCode != 200) {
                    Log.d("error message",message);
                }
            }else{
                Log.d("JSON error","is null");
            }
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch ( JSONException e) {
            e.printStackTrace();
        }
    }



}
