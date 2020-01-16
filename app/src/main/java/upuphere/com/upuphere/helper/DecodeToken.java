package upuphere.com.upuphere.helper;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import static com.android.volley.VolleyLog.TAG;


public class DecodeToken {

    private int getExp() {
        return exp;
    }

    private int exp;


    public static Boolean jwtTokenStillValid(String JWTEncoded) {
        String[] split = JWTEncoded.split("\\.");
        Log.e(TAG, "Header: " + getJson(split[0]));
        Log.e(TAG, "Body: " + getJson(split[1]));
        return getJson(split[1]);
    }

    private static Boolean getJson(String strEncoded){
        String str_dec = "";
        int expstring = 0;
        try {
            byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
            str_dec = new String(decodedBytes, "UTF-8");
            Gson gson = new Gson();
            DecodeToken exp = gson.fromJson(str_dec,DecodeToken.class);
            expstring = exp.getExp();

            Log.d("EXP",String.valueOf(expstring));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return checkIsJwtStillValid(String.valueOf(expstring));
    }

    private static boolean checkIsJwtStillValid(String exp){
        long expLong = Long.parseLong(exp) * 1000;
        //get current TS
        long currentTime = System.currentTimeMillis();
        //check
        return expLong >= currentTime;
    }

}
