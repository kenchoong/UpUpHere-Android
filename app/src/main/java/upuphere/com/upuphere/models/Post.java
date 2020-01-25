package upuphere.com.upuphere.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post {

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


    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("_cls")
    @Expose
    private String cls;
    @SerializedName("post_title")
    @Expose
    private String postTitle;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("in_room")
    @Expose
    private String inRoom;
    @SerializedName("comment")
    @Expose
    private List<Comment> comment = null;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("image_path")
    @Expose
    private String imagePath;

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

    public String getInRoom() {
        return inRoom;
    }

    public void setInRoom(String inRoom) {
        this.inRoom = inRoom;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
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
}
