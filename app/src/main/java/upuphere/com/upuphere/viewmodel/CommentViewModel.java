package upuphere.com.upuphere.viewmodel;

import android.view.View;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.repositories.PostRepo;
import upuphere.com.upuphere.repositories.RoomRepo;

public class CommentViewModel extends ViewModel {

    public interface CommentInterface{
        void onSend();
    }

    public CommentInterface commentInterface;

    public void setCommentInterface(CommentInterface mInterface){
        this.commentInterface = mInterface;
    }

    public String commentText;

    public void onSendButtonClick(View view){
        commentInterface.onSend();
    }

    public LiveData<List<CommentModel>> getCommentLiveData(List<Post> posts, String postId){
        return PostRepo.getInstance().getCommentMutableLiveData(posts,postId);
    }
}
