package upuphere.com.upuphere.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import upuphere.com.upuphere.models.NotificationModel;

@Dao
public interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(NotificationModel notificationModel);

    @Query("DELETE FROM notification_table")
    void deleteAll();

    @Delete
    void deleteNotification(NotificationModel notificationModel);

    @Query("SELECT * from notification_table ORDER BY id DESC")
    LiveData<List<NotificationModel>> getAllNotification();
}
