package upuphere.com.upuphere.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.PostItemRowBinding;
import upuphere.com.upuphere.models.Post;

public class PostAdapter  extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> post;
    private LayoutInflater layoutInflater;
    private PostAdapterListener listener;

    public PostAdapter(PostAdapterListener listener) {
        this.listener = listener;
    }

    public void setPost(List<Post> postList){
        //Log.d("POST HERE","GET CALLED");
        this.post = postList;
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
        holder.binding.setData(post.get(position));

        holder.binding.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentClicked(post.get(position));
            }
        });

        holder.binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShareClicked(post.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (post != null) {
            //Log.d("post SIZE",String.valueOf(post.size()));
            return post.size();
        } else {
            //Log.d("post SIZE","0");
            return 0;
        }
    }


    public interface PostAdapterListener{
        void onCommentClicked(Post post);

        void onShareClicked(Post post);
    }
}
