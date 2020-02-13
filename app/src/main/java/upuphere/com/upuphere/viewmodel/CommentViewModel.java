package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.repositories.PostRepo;
import upuphere.com.upuphere.repositories.RoomRepo;

public class CommentViewModel extends AndroidViewModel {
    PostRepo postRepo;

    public CommentViewModel(@NonNull Application application) {
        super(application);
        postRepo = new PostRepo(application);
    }

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
        return postRepo.getCommentMutableLiveData(posts,postId);
    }

    public void appendNewCommentToMutableLiveData(List<Post> posts,String postId,CommentModel comment){
        postRepo.appenNewCommentToMutableLiveData(posts,postId,comment);
    }

    public void createComment(String postId, String commentText){
        postRepo.createComment(postId, commentText, new StringCallBack() {
            @Override
            public void success(String item) {
                Log.d("COMMENT",item);
            }

            @Override
            public void showError(String error) {

            }
        });
    }
}
