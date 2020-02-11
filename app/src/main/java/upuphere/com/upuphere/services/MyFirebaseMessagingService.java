package upuphere.com.upuphere.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import upuphere.com.upuphere.MainActivity;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.NotificationUtils;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.repositories.UserRepo;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Log.d("New Token 1",newToken);

        new PrefManager(AppController.getContext()).setFirebaseToken(newToken);
        UserRepo.getInstance().updateFirebaseTokenToServer(AppController.getContext(),newToken);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // todo:; handle notification type
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

        }
    }



    private void handleDataMessage(JSONObject json) {
        // todo:: parse the data inside the json

        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String timeStamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");
            String postId = payload.getString("post_id");

            //pass to Main Fragment to save in local database
            Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message",message);
            pushNotification.putExtra("post_id", postId);
            pushNotification.putExtra("timestamp",timeStamp);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            if(AppController.isAppInForeground){
                // app in Foreground
                Log.d(TAG, "App in foreground" + "YES");
                new NotificationUtils(getApplicationContext()).playNotificationSound();

            }else{
                // app in background,show notification in system tray
                Log.d(TAG, "App in foreground" + "No");

                String  channelId = getString(R.string.default_notification_channel_id);
                String channelName = getString(R.string.discussion_channel);

                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("post_id",postId);
                resultIntent.putExtra("timestamp",timeStamp);

                showTextNotification(getApplicationContext(),title,message,channelId,channelName,timeStamp,resultIntent);
            }


        } catch (JSONException e) {
            Log.e(TAG,"JSON Exception" + e.getMessage());
        }catch (Exception e){
           Log.e(TAG,"Any Other Exception" + e.getMessage());
        }
    }

    NotificationUtils notificationUtils;
    private void showTextNotification(Context context,String title,String message,String channelId,String channelName,String timestamp,Intent intent){
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showTextNotification(title,message,channelId,channelName,timestamp,intent);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
