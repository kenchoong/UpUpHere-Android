package upuphere.com.upuphere.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import upuphere.com.upuphere.models.NotificationModel;
import upuphere.com.upuphere.repositories.NotificationRepo;

public class NotificationViewModel extends AndroidViewModel {

    private NotificationRepo notificationRepo;

    private LiveData<List<NotificationModel>> notificationListLiveData;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        notificationRepo = new NotificationRepo(application);
        notificationListLiveData = notificationRepo.getNotificationList();
    }

    public LiveData<List<NotificationModel>> getNotificationList(){
        return  notificationListLiveData;
    }

    public void addNewNotificationInLocalDb(NotificationModel notificationModel){
        notificationRepo.addNotificationToDB(notificationModel);
    }

    public void deleteNotification(NotificationModel notificationModel){
        notificationRepo.deleteNotification(notificationModel);
    }


}
