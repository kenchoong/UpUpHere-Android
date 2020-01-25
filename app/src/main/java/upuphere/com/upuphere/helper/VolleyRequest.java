package upuphere.com.upuphere.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import upuphere.com.upuphere.app.AppController;


public class VolleyRequest {

    public interface ResponseCallBack{
        void onSuccess(JSONObject response);

        void onError(String error);
    }

    public static JSONObject getParams(String[] keys,String[] values){
        //String[] key = new String[];

        Map<String, String> params = new HashMap<>();
        if (keys.length != 0) {
            for (int i = 0; i < keys.length; i++) {
                params.put(keys[i], values[i]);
            }
        }
         return new JSONObject(params);
    }

    public static JsonObjectRequest postJsonAccessRequestWithoutRetry(String url, JSONObject params, final ResponseCallBack responseCallBack) {
        JsonObjectRequest jsonObjectRequest =  new JsonObjectRequest(Request.Method.POST, url, params,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("onResponse", response.toString());
                responseCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
                //responseCallBack.onError(error);
                responseCallBack.onError(parseVolleyError(error));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return volleyAccessClient();
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonObjectRequest;
    }

    public static JsonObjectRequest postJsonRefreshRequestWithoutRetry(final String url, JSONObject params, final ResponseCallBack responseCallBack){
        JsonObjectRequest jsonObjectRequest =  new JsonObjectRequest(Request.Method.POST, url, params,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("onResponse", response.toString());
                responseCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
                //responseCallBack.onError(error);
                responseCallBack.onError(parseVolleyError(error));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return volleyRefreshClient();
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsonObjectRequest;
    }

    public static JsonObjectRequest getJsonAccessRequestWithoutRetry(String url, final ResponseCallBack responseCallBack){

        return new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse",response.toString());
                responseCallBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onErrorResponse",error.toString());
                responseCallBack.onError(parseVolleyError(error));
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");

                return headers;
            }
        };
    }

    private static Map<String,String> volleyAccessClient(){
        //String accessToken = new PrefManager(AppController.getContext()).getUserAccessToken();
        //String userId = new Gson().fromJson(new PrefManager(AppController.getContext()).getUserDetailsObject())
        Log.d("Login","Headers");

        String accessToken = new PrefManager(AppController.getContext()).getUserAccessToken();

        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type","application/json");

        if (accessToken != null) {
            headers.put("Authorization", "Bearer " + accessToken);
        }

        Log.d("Login",headers.toString());
        return headers;
    }

    private static Map<String,String> volleyRefreshClient(){
        //String refreshToken = new PrefManager(AppController.getContext()).getUserRefreshToken();
        String refreshToken = new PrefManager(AppController.getContext()).getUserRefreshToken();

        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type","application/json");
        headers.put("Authorization", "Bearer " + refreshToken);
        return headers;
    }

    private static String parseVolleyError(VolleyError error) {
        try {
            Log.d("Json error",error.toString());
            if(error.networkResponse != null){
                int statusCode = error.networkResponse.statusCode;

                String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject data = new JSONObject(responseBody);
                String message = data.optString("message");

                if (statusCode != 200) {
                    //Log.d("error message",message);
                    return message;
                }


            }else{
                Log.d("JSON error","is null");
                return "Error message not specified";
            }
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch ( JSONException e) {
            e.printStackTrace();
            return "Error";
        }
        return "Not specified";
    }
}
