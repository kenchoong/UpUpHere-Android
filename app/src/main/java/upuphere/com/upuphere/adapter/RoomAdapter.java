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
import upuphere.com.upuphere.databinding.RoomItemRowBinding;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.RoomModel;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private List<AllRooms> roomList;
    private LayoutInflater layoutInflater;
    private RoomAdapterListener listener;

    public RoomAdapter(RoomAdapterListener listener) {
        this.listener = listener;
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {
        RoomItemRowBinding binding;

        public RoomViewHolder(RoomItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }
    }

    public void setRoomList(List<AllRooms> room){
        this.roomList = room;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        RoomItemRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.room_item_row,parent,false);

        return new RoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, final int position) {
        holder.binding.setModel(roomList.get(position));
        holder.binding.roomProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRoomClicked(roomList.get(position));
            }
        });

        holder.binding.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMoreButtonClicked(roomList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {

        if (roomList != null) {
            //Log.d("Room SIZE",String.valueOf(roomList.size()));
            return roomList.size();
        } else {
            //Log.d("Room SIZE","0");
            return 0;
        }
    }

    public interface RoomAdapterListener{
        void onRoomClicked(AllRooms room);

        void onMoreButtonClicked(AllRooms rooms);
    }


}
