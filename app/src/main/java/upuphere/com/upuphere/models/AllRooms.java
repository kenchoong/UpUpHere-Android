package upuphere.com.upuphere.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "room_table")
public class AllRooms extends BaseObservable implements Parcelable {

    @NonNull
    @PrimaryKey
    @SerializedName("_id")
    @Expose
    private String id = "";

    @ColumnInfo(name = "_cls")
    @SerializedName("_cls")
    @Expose
    private String cls;

    @ColumnInfo(name = "room_public_id")
    @SerializedName("room_public_id")
    @Expose
    private String roomPublicId;

    @ColumnInfo(name = "room_name")
    @SerializedName("room_name")
    @Expose
    private String roomName;

    @ColumnInfo(name = "room_profile_image")
    @SerializedName("room_profile_image")
    @Expose
    private String roomProfileImage;

    @ColumnInfo(name = "room_description")
    @SerializedName("room_description")
    @Expose
    private String roomDescription;

    @ColumnInfo(name = "created_by")
    @SerializedName("created_by")
    @Expose
    private String createdBy;

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @ColumnInfo(name = "active_status")
    @SerializedName("active_status")
    @Expose
    private Boolean activeStatus;

    public AllRooms(Parcel in) {
        id = Objects.requireNonNull(in.readString());
        cls = in.readString();
        roomPublicId = in.readString();
        roomName = in.readString();
        roomProfileImage = in.readString();
        roomDescription = in.readString();
        createdBy = in.readString();
        createdAt = in.readString();
        byte tmpActiveStatus = in.readByte();
        activeStatus = tmpActiveStatus == 0 ? null : tmpActiveStatus == 1;
    }

    public static final Creator<AllRooms> CREATOR = new Creator<AllRooms>() {
        @Override
        public AllRooms createFromParcel(Parcel in) {
            return new AllRooms(in);
        }

        @Override
        public AllRooms[] newArray(int size) {
            return new AllRooms[size];
        }
    };

    public AllRooms() {

    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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
        parcel.writeString(id);
        parcel.writeString(cls);
        parcel.writeString(roomPublicId);
        parcel.writeString(roomName);
        parcel.writeString(roomProfileImage);
        parcel.writeString(roomDescription);
        parcel.writeString(createdBy);
        parcel.writeString(createdAt);
        parcel.writeByte((byte) (activeStatus == null ? 0 : activeStatus ? 1 : 2));
    }
}
