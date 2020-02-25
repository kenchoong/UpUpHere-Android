package upuphere.com.upuphere.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    public SharedPreferences getPref() {
        return pref;
    }

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    private static final String PREF_NAME = "UpUpHere";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_USER_AGREED_TERM = "UserAgreedTerm";

    public static final String IS_LOGGED_IN = "IsLoggedIn";

    private static final String USER_ID= "user_id";
    private static final String USERNAME = "username";
    private static final String USER_REAL_ID = "user_real_id";
    private static final String USER_DEVICE_ID= "user_device_id";
    private static final String USER_SESSION_ID= "user_session_id";
    private static final String USER_ACCESS_TOKEN = "access_token";
    private static final String USER_REFRESH_TOKEN = "refresh_token";

    private static final String USER_DETAILS_GSON = "user_details_gson";

    private static final String FIREBASE_TOKEN = "firebase_token";
    // shared pref mode
    int PRIVATE_MODE = 0;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean isUserAgreeTerm(){
        return pref.getBoolean(IS_USER_AGREED_TERM,false);
    }

    public void setIsUserAgreedTerm(boolean isUserAgreedTerm){
        editor.putBoolean(IS_USER_AGREED_TERM, isUserAgreedTerm);
        editor.commit();
    }

    public void setIsLoggedIn(boolean isLoggedIn){
        editor.putBoolean(IS_LOGGED_IN,isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN,false);
    }

    public void setUserAccessToken(String accessToken){
        editor.putString(USER_ACCESS_TOKEN,accessToken);
        editor.commit();
    }

    public void setUserRefreshToken(String refreshToken){
        editor.putString(USER_REFRESH_TOKEN,refreshToken);
        editor.commit();
    }

    public String getUserAccessToken(){
        return pref.getString(USER_ACCESS_TOKEN,null);
    }

    public String getUserRefreshToken(){
        return pref.getString(USER_REFRESH_TOKEN,null);
    }

    public void setUserID(String userID){
        editor.putString(USER_ID,userID);
        editor.commit();
    }

    public String getUserId(){
        return pref.getString(USER_ID,null);
    }

    public void setUserRealId(String userID){
        editor.putString(USER_REAL_ID,userID);
        editor.commit();
    }

    public String getUserRealId(){
        return pref.getString(USER_REAL_ID,null);
    }

    public String getUsername(){
        return pref.getString(USERNAME,null);
    }

    public void setUsername(String username){
        editor.putString(USERNAME,username);
        editor.commit();
    }

    public String getUserDeviceId(){
        return pref.getString(USER_DEVICE_ID,null);
    }

    public void setUserDeviceId(String userDeviceId){
        editor.putString(USER_DEVICE_ID,userDeviceId);
        editor.commit();
    }

    public String getUserSessionId(){
        return pref.getString(USER_SESSION_ID,null);
    }

    public void setUserSessionId(String userSessionId){
        editor.putString(USER_SESSION_ID,userSessionId);
        editor.commit();
    }

    public void removeAllSharedPrefrences(){
        editor.clear();
        editor.commit();
    }

    public String getUserDetailsObject(){
        return pref.getString(USER_DETAILS_GSON,null);
    }

    public void setUserDetailsGson(String userDetailsGson){
        editor.putString(USER_DETAILS_GSON,userDetailsGson);
        editor.commit();
    }

    public String getFirebaseToken(){
        return pref.getString(FIREBASE_TOKEN,null);
    }

    public void setFirebaseToken(String myFirebaseToken){
        editor.putString(FIREBASE_TOKEN,myFirebaseToken);
        editor.commit();
    }

    private SharedPreferenceBooleanLiveData sharedPreferenceLiveData;

    public SharedPreferenceBooleanLiveData getSharedPrefs(){
        return sharedPreferenceLiveData;
    }

    public void setSharedPreferences(String key, boolean value) {
        SharedPreferences userDetails = _context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userDetails.edit();
        editor.putBoolean(key, value);
        editor.apply();
        sharedPreferenceLiveData = new SharedPreferenceBooleanLiveData(userDetails,key,value);
    }

}
