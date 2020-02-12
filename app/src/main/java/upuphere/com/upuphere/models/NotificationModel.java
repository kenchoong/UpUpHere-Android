package upuphere.com.upuphere.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_table")
public class NotificationModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "notification_message")
    public String notificationMessage;

    @ColumnInfo(name = "timestamp")
    public String timestamp;

    @ColumnInfo(name= "post_id")
    private String postId;

    public NotificationModel(@NonNull String notificationMessage, String timestamp, String postId) {
        this.notificationMessage = notificationMessage;
        this.timestamp = timestamp;
        this.postId = postId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(@NonNull String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


}
