package upuphere.com.upuphere.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import upuphere.com.upuphere.BuildConfig;

public class AppController extends Application implements LifecycleObserver {

    public static final String TAG = AppController.class.getSimpleName();

    //all this is for volley
    private RequestQueue mRequestQueue;
    private static AppController mInstance;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        /*
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        JodaTimeAndroid.init(this);

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, true);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "3.5.1");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=com.minetech.messenglassapp");

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });

         */
    }

    public static boolean isAppInForeground = false;
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrouded(){
        isAppInForeground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public  void onAppForegrounded(){
        isAppInForeground = true;
    }

    public static Context getContext(){
        return mContext;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public boolean internetConnectionAvailable(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }else {
            return false;
        }

    }
}
