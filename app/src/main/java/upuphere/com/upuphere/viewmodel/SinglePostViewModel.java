package upuphere.com.upuphere.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import upuphere.com.upuphere.Interface.StringCallBack;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.repositories.PostRepo;

public class SinglePostViewModel extends AndroidViewModel {
    private PostRepo postRepo;

    public SinglePostViewModel(@NonNull Application application) {
        super(application);
        postRepo = new PostRepo(application);
    }

    public interface SinglePostInterface {
        void onSend();

        void onPostOrUserBlock(String message);

        void onClickUnHideButton();
    }

    public SinglePostInterface commentInterface;

    public void setCommentInterface(SinglePostInterface mInterface){
        this.commentInterface = mInterface;
    }

    public String commentText;

    public void onSendButtonClick(View view){
        commentInterface.onSend();
    }

    public void onUnhideButtonClick(View view){
        commentInterface.onClickUnHideButton();
    }

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private void setIsLoading(boolean isLoadingOrNot){
        isLoading.setValue(isLoadingOrNot);
    }

    public LiveData<List<Post>> getSinglePostByPostId(String postId){
        return postRepo.getSinglePostByPostId(postId, new StringCallBack() {
            @Override
            public void success(String item) {
                //do nothing
            }

            @Override
            public void showError(String error) {
                Log.d("SINGLEPOSTFRAGMENT",error);
                commentInterface.onPostOrUserBlock(error);
            }
        });
    }

    public void unHidePost(String postId, final StringCallBack callBack){
        setIsLoading(true);
        postRepo.unHidePost(postId, new StringCallBack() {
            @Override
            public void success(String item) {
                callBack.success(item);
                setIsLoading(false);
            }

            @Override
            public void showError(String error) {
                callBack.showError(error);
                setIsLoading(false);
            }
        });
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

    public void blockUserOrHidePost(String postOrUserId, int operationType, final StringCallBack callback){
        setIsLoading(true);
        postRepo.blockUserOrHidePost(postOrUserId, operationType, new StringCallBack() {
            @Override
            public void success(String item) {
                setIsLoading(false);
                callback.success(item);
            }

            @Override
            public void showError(String error) {
                setIsLoading(false);
                callback.showError(error);
            }
        });
    }
}
