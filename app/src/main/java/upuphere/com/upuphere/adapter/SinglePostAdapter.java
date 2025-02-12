package upuphere.com.upuphere.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.CommentItemRowBinding;
import upuphere.com.upuphere.databinding.PostItemRowBinding;
import upuphere.com.upuphere.databinding.SinglePostTypeItemRowBinding;
import upuphere.com.upuphere.models.CommentModel;
import upuphere.com.upuphere.models.Post;

public class SinglePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int POST_TYPE = 1111;
    private static final int COMMENT_TYPE = 2222;

    private LayoutInflater layoutInflater;

    public SinglePostAdapter(SinglePostAdapterListener listener) {
        this.listener = listener;
    }

    private SinglePostAdapterListener listener;

    private List<CommentModel> commentList;
    private List<Post> post;
    private int size = 0;

    public void setPost(List<Post> postList){
        this.post = postList;
        notifyDataSetChanged();
    }

    public void setComment(List<CommentModel> comment){
        this.commentList = comment;
        notifyDataSetChanged();
    }

    public void removeHidedComment(CommentModel comment){
        commentList.remove(comment);
        notifyDataSetChanged();
    }

    public void removeCommentCreatedByBlockedUser(String userId){
        List<CommentModel> shouldRemoveComment = new ArrayList<>();
        for(CommentModel comment : commentList){
            if(comment.getCommenterUserId().equals(userId)){
                shouldRemoveComment.add(comment);
            }
        }
        commentList.removeAll(shouldRemoveComment);
        notifyDataSetChanged();
    }

    public void removeAllData(){
        if(commentList !=null & post != null){
            commentList.clear();
            post.clear();
            notifyDataSetChanged();
        }
    }

    public interface SinglePostAdapterListener{
        void onPostMoreButtonClick(Post post);

        void onCommentMoreButtonClick(CommentModel comment);
    }

    public class SinglePostViewHolder extends RecyclerView.ViewHolder{
        SinglePostTypeItemRowBinding binding;

        public SinglePostViewHolder(SinglePostTypeItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;

        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        CommentItemRowBinding commentBinding;

        public CommentViewHolder(CommentItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.commentBinding = itemRowBinding;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return POST_TYPE;
        }else{
            return COMMENT_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        switch (viewType){
            case COMMENT_TYPE:
                CommentItemRowBinding commentItemRowBinding = DataBindingUtil.inflate(layoutInflater,R.layout.comment_item_row,parent,false);
                return new SinglePostAdapter.CommentViewHolder(commentItemRowBinding);
            case POST_TYPE:
            default:
                SinglePostTypeItemRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.single_post_type_item_row,parent,false);
                return new SinglePostAdapter.SinglePostViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        int viewType = holder.getItemViewType();
        switch (viewType){
            case COMMENT_TYPE:
                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                commentViewHolder.commentBinding.setData(commentList.get(position-1)); // minus 1,cause 1 is single post


                ((CommentViewHolder) holder).commentBinding.moreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onCommentMoreButtonClick(commentList.get(position));
                    }
                });

                break;
            case POST_TYPE:
                default:
                    SinglePostViewHolder singlePostViewHolder = (SinglePostViewHolder) holder;
                    singlePostViewHolder.binding.setData(post.get(position));

                    ((SinglePostViewHolder) holder).binding.moreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onPostMoreButtonClick(post.get(position));
                        }
                    });
                break;
        }

    }

    @Override
    public int getItemCount() {
        if(post != null && commentList != null){
            return post.size() + commentList.size();
        }
        else if(post != null){
            return 1;
        }
        else{
            return 0;
        }
    }
}
