package upuphere.com.upuphere.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.PostItemRowBinding;
import upuphere.com.upuphere.models.Post;

public class PostAdapter  extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private LayoutInflater layoutInflater;
    private PostAdapterListener listener;

    public PostAdapter(PostAdapterListener listener) {
        this.listener = listener;
    }

    public void setPost(List<Post> postList){
        //Log.d("POST HERE","GET CALLED");
        this.postList = postList;
        notifyDataSetChanged();
    }

    public void removeHidedPost(Post post){
        postList.remove(post);
        notifyDataSetChanged();
    }

    public void removeAllPost(){
        if (postList != null) {
            postList.clear();
            notifyDataSetChanged();
        }
    }

    public void removePostCreatedByBlockedUser(String userId){
        List<Post> shouldRemovePost = new ArrayList<>();
        for(Post post : postList){
            if(post.getAuthorUserId().equals(userId)){
                shouldRemovePost.add(post);
            }
        }
        postList.removeAll(shouldRemovePost);
        notifyDataSetChanged();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        PostItemRowBinding binding;

        public PostViewHolder(PostItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;

        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        PostItemRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.post_item_row,parent,false);

        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, final int position) {
        holder.binding.setData(postList.get(position));

        holder.binding.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentClicked(postList.get(position));
            }
        });
        /*
        holder.binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShareClicked(postList.get(position));
            }
        });*/

        holder.binding.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMoreButtonClicked(postList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (postList != null) {
            //Log.d("post SIZE",String.valueOf(post.size()));
            return postList.size();
        } else {
            //Log.d("post SIZE","0");
            return 0;
        }
    }


    public interface PostAdapterListener{
        void onCommentClicked(Post post);

        void onShareClicked(Post post);

        void onMoreButtonClicked(Post post);
    }
}
