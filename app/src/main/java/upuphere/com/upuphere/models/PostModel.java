package upuphere.com.upuphere.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostModel {

    @SerializedName("post_in_rooms")
    @Expose
    private List<Post> postInRooms = null;

    public List<Post> getPost() {
        return postInRooms;
    }

    public void setPost(List<Post> postInRooms) {
        this.postInRooms = postInRooms;
    }

}
