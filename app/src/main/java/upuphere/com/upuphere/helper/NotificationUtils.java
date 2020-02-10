package upuphere.com.upuphere.helper;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.app.AppConfig;
import upuphere.com.upuphere.app.AppController;

public class NotificationUtils {

    private Context mContext;

    public NotificationUtils(Context mContext){
        this.mContext = mContext;
    }

    public void showTextNotification(String title, String message, String channelId,String channelName, String timeStamp, Intent intent){
        showNotificationMessage(title,message,channelId,channelName,timeStamp,intent,null);
    }

    /**Here is the main entry point to decide what type of notification to send
     * Either is text,image,expandable and so on
     * depend on the logic and criteria
     * For now, we only send text
     * Add more params if needed*/
    private void showNotificationMessage(String title, String message, String channelId,String channelName, String timeStamp, Intent intent,String imageUrl){
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        int icon = R.mipmap.ic_launcher;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, channelId);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        int priority = NotificationCompat.PRIORITY_HIGH;

        showTextOnlyNotification(mBuilder,priority,icon,title,message,timeStamp,channelId,channelName ,resultPendingIntent,sound);
    }

    private void showTextOnlyNotification(NotificationCompat.Builder mBuilder,int priority,int icon, String title, String message, String timeStamp,String channelId,
                                            String channelName,PendingIntent resultPendingIntent, Uri notificationSound){
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(notificationSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setPriority(priority)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        Objects.requireNonNull(notificationManager).notify(AppConfig.NOTIFICATION_TEXT, notification);
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, sound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
