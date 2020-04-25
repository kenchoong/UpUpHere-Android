package upuphere.com.upuphere.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import upuphere.com.upuphere.R;
import upuphere.com.upuphere.databinding.RoomItemRowBinding;
import upuphere.com.upuphere.helper.UnifiedNativeAdViewHolder;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.RoomAdsData;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    List<RoomAdsData> roomAdsDataList;

    public static final int UNIFIED_ADS = 1;

    public static final int ROOM_TYPE = 2;

    private RoomAdapterListener listener;

    public RoomAdapter(RoomAdapterListener listener) {
        this.listener = listener;
        setHasStableIds(true);
    }

    public void setRoomAdsDataList(List<RoomAdsData> roomAdsDataForRecyclerView) {
        this.roomAdsDataList = roomAdsDataForRecyclerView;
        notifyDataSetChanged();
    }

    public void removeHidedRoom(int position) {
        roomAdsDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeRoomCreatedByBlockedUser(String roomOwnerUserId) {
        List<RoomAdsData> shouldRemoveRoom = new ArrayList<>();
        for(RoomAdsData roomAdsData: roomAdsDataList){
            if(roomAdsData.getRooms() != null){
                if(roomAdsData.getRooms().getRoomOwnerUserId().equals(roomOwnerUserId)){
                    shouldRemoveRoom.add(roomAdsData);
                }
            }
        }

        roomAdsDataList.removeAll(shouldRemoveRoom);
        //notifyDataSetChanged();
        notifyItemRangeRemoved(0,shouldRemoveRoom.size());
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {
        RoomItemRowBinding binding;

        public RoomViewHolder(RoomItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        switch (viewType){
            case UNIFIED_ADS:
                View unifiedNativeLayoutView = layoutInflater.inflate(R.layout.ad_room_fragment, parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case ROOM_TYPE:
            default:
                RoomItemRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.room_item_row,parent,false);
                return new RoomViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        int viewType = getItemViewType(position);

        switch (viewType){
            case UNIFIED_ADS:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) roomAdsDataList.get(position).getAds();
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) viewHolder).getAdView());
                break;
            case ROOM_TYPE:
                final RoomViewHolder holder = (RoomViewHolder) viewHolder;

                holder.binding.setModel(roomAdsDataList.get(holder.getAdapterPosition()).getRooms());
                holder.binding.roomProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onRoomClicked(roomAdsDataList.get(holder.getAdapterPosition()).getRooms());
                    }
                });

                holder.binding.moreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onMoreButtonClicked(roomAdsDataList.get(holder.getAdapterPosition()).getRooms(),holder.getAdapterPosition());
                    }
                });
                break;
        }
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    @Override
    public int getItemViewType(int position) {
        if(roomAdsDataList.get(position).getType() == 1){
            return UNIFIED_ADS;
        }else{
            return ROOM_TYPE;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        if(roomAdsDataList != null){
            return roomAdsDataList.size();
        }else{
            return 0;
        }
    }

    public interface RoomAdapterListener{
        void onRoomClicked(AllRooms room);

        void onMoreButtonClicked(AllRooms rooms,int position);
    }
}
