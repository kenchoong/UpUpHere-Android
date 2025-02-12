package upuphere.com.upuphere.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "post_table")
public class Post implements Parcelable {

    /**
     * {
     *     "post_in_rooms": [
     *         {
     *             "_id": "5e2c6205135ecb537e641ab2",
     *             "_cls": "PostModel.ImagePost",
     *             "post_title": "I love u",
     *             "author": "5e17115ced3adf3270536b3f",
     *             "in_room": "5e20644a32287abadbf1a5c8",
     *             "comment": [
     *                 {
     *                     "_cls": "CommentModel",
     *                     "text_comment": "I love you",
     *                     "user": "5e17115ced3adf3270536b3f",
     *                     "created_at": "2020-01-25 21:39:01"
     *                 }
     *             ],
     *             "created_at": "2020-01-25 21:39:01",
     *             "image_path": "/2020-01-25_234301.693641.jpg"
     *         }
     *     ]
     * }
     * */

    @NonNull
    @PrimaryKey
    @SerializedName("_id")
    @Expose
    private String id = "";

    @ColumnInfo(name = "_cls")
    @SerializedName("_cls")
    @Expose
    private String cls;

    @ColumnInfo(name = "post_title")
    @SerializedName("post_title")
    @Expose
    private String postTitle;

    @ColumnInfo(name = "author")
    @SerializedName("author")
    @Expose
    private String author;

    @ColumnInfo(name = "author_user_id")
    @SerializedName("author_user_id")
    @Expose
    private String authorUserId;

    @ColumnInfo(name = "in_room")
    @SerializedName("in_room")
    @Expose
    private String inRoom;

    @ColumnInfo(name="comment")
    @SerializedName("comment")
    @Expose
    private List<CommentModel> comment = null;

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @ColumnInfo(name = "image_path")
    @SerializedName("image_path")
    @Expose
    private String imagePath;

    public Post(){

    }

    protected Post(Parcel in) {
        id = Objects.requireNonNull(in.readString());
        cls = in.readString();
        postTitle = in.readString();
        author = in.readString();
        authorUserId = in.readString();
        inRoom = in.readString();
        createdAt = in.readString();
        imagePath = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

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

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(String authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getInRoom() {
        return inRoom;
    }

    public void setInRoom(String inRoom) {
        this.inRoom = inRoom;
    }

    public List<CommentModel> getComment() {
        return comment;
    }

    public void setComment(List<CommentModel> comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(cls);
        parcel.writeString(postTitle);
        parcel.writeString(author);
        parcel.writeString(inRoom);
        parcel.writeString(createdAt);
        parcel.writeString(imagePath);
    }
}
