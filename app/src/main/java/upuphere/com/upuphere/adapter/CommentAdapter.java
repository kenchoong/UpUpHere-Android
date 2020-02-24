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
import upuphere.com.upuphere.databinding.CommentItemRowBinding;
import upuphere.com.upuphere.models.CommentModel;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private List<CommentModel> commentList;
    private LayoutInflater layoutInflater;

    public CommentAdapter(CommentAdapterListner listener) {
        this.listener = listener;
    }

    private CommentAdapterListner listener;

    public void setComment(List<CommentModel> commentList){
        this.commentList = commentList;
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

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        CommentItemRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.comment_item_row,parent,false);

        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, final int position) {
        holder.binding.setData(commentList.get(position));

        holder.binding.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMoreButtonClicked(commentList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (commentList != null) {
            //Log.d("comment SIZE",String.valueOf(comment.size()));
            return commentList.size();
        } else {
            //Log.d("comment SIZE","0");
            return 0;
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        CommentItemRowBinding binding;

        public CommentViewHolder(CommentItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;

        }
    }

    public interface CommentAdapterListner{
        void onMoreButtonClicked(CommentModel comment);
    }
}
