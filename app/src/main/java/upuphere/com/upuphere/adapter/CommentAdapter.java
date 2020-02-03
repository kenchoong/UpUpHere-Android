package upuphere.com.upuphere.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.CommentItemRowBinding;
import upuphere.com.upuphere.models.CommentModel;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private List<CommentModel> comment;
    private LayoutInflater layoutInflater;

    public void setComment(List<CommentModel> commentList){
        this.comment = commentList;
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
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.binding.setData(comment.get(position));
    }

    @Override
    public int getItemCount() {
        if (comment != null) {
            //Log.d("comment SIZE",String.valueOf(comment.size()));
            return comment.size();
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
}
