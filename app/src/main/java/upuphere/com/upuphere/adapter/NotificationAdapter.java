package upuphere.com.upuphere.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.FragmentNotificationBinding;
import upuphere.com.upuphere.databinding.NotificationItemRowBinding;
import upuphere.com.upuphere.models.NotificationModel;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    List<NotificationModel> notificationList;
    private LayoutInflater layoutInflater;
    private NotificationAdapterListener listener;

    public void setNotificationList(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    public NotificationAdapter(NotificationAdapterListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        NotificationItemRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.notification_item_row,parent,false);

        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, final int position) {
        holder.nItemRowBinding.setData(notificationList.get(position));

        holder.nItemRowBinding.notificationContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onNotificationClicked(notificationList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (notificationList != null) {
            //Log.d("comment SIZE",String.valueOf(comment.size()));
            return notificationList.size();
        } else {
            //Log.d("comment SIZE","0");
            return 0;
        }
    }

    public interface NotificationAdapterListener{
        void onNotificationClicked(NotificationModel notification);
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        NotificationItemRowBinding nItemRowBinding;

        public NotificationViewHolder(NotificationItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.nItemRowBinding = itemRowBinding;
        }
    }
}
