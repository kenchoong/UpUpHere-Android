package upuphere.com.upuphere.repositories;

import android.app.Application;
import android.util.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import upuphere.com.upuphere.database.NotificationDao;
import upuphere.com.upuphere.database.UpUpHereDatabase;
import upuphere.com.upuphere.models.NotificationModel;

public class NotificationRepo {

    private NotificationDao notificationDao;
    private LiveData<List<NotificationModel>> notificationList;

    public NotificationRepo(Application application){
        UpUpHereDatabase db = UpUpHereDatabase.getDatabase(application);
        notificationDao = db.notificationDao();
        notificationList = notificationDao.getAllNotification();
    }

    public LiveData<List<NotificationModel>> getNotificationList(){
        return  notificationList;
    }

    public void addNotificationToDB(final NotificationModel notificationModel){
        UpUpHereDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                notificationDao.insert(notificationModel);
            }
        });
    }

    public void deleteNotification(final NotificationModel model){
        UpUpHereDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                notificationDao.deleteNotification(model);
            }
        });
    }
}
