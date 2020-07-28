package upuphere.com.upuphere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import upuphere.com.upuphere.R;
import upuphere.com.upuphere.models.PhotoList;
import upuphere.com.upuphere.models.RoomAdsData;

public class SelectPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private SelectPhotoListener listener;
    List<String> photoList;
    Context mContext;

    public SelectPhotoAdapter(SelectPhotoListener listener, Context context) {
        this.listener = listener;
        this.mContext = context;
    }

    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    public interface SelectPhotoListener{
        void onSelectedPhoto();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photoImageView);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View photoView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item_row, parent, false);
        return new PhotoViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;

        Glide
                .with(mContext)
                .load(photoList.get(position))
                .into(photoViewHolder.photo);

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}
