package upuphere.com.upuphere.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CommentModel{

    @SerializedName("_cls")
    @Expose
    private String cls;

    @SerializedName("text_comment")
    @Expose
    private String textComment;

    @SerializedName("user")
    @Expose
    private String user;

    @SerializedName("commenter_user_id")
    @Expose
    private String commenterUserId;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("comment_id")
    @Expose
    private String commentId;

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getTextComment() {
        return textComment;
    }

    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCommenterUserId() {
        return commenterUserId;
    }

    public void setCommenterUserId(String commenterUserId) {
        this.commenterUserId = commenterUserId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
