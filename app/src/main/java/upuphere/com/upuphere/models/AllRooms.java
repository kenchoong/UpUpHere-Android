package upuphere.com.upuphere.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.databinding.BaseObservable;

public class AllRooms extends BaseObservable implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("_cls")
    @Expose
    private String cls;
    @SerializedName("room_public_id")
    @Expose
    private String roomPublicId;
    @SerializedName("room_name")
    @Expose
    private String roomName;
    @SerializedName("room_profile_image")
    @Expose
    private String roomProfileImage;
    @SerializedName("room_description")
    @Expose
    private String roomDescription;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("active_status")
    @Expose
    private Boolean activeStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getRoomPublicId() {
        return roomPublicId;
    }

    public void setRoomPublicId(String roomPublicId) {
        this.roomPublicId = roomPublicId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomProfileImage() {
        return roomProfileImage;
    }

    public void setRoomProfileImage(String roomProfileImage) {
        this.roomProfileImage = roomProfileImage;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
