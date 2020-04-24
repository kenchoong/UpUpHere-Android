package upuphere.com.upuphere.adapter;

import android.util.Log;
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
import upuphere.com.upuphere.databinding.PostItemRowBinding;
import upuphere.com.upuphere.helper.UnifiedNativeAdViewHolder;
import upuphere.com.upuphere.models.Post;
import upuphere.com.upuphere.models.PostAdsData;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private List<Post> postList;
    public List<PostAdsData> postAdsDataList;
    private LayoutInflater layoutInflater;
    private PostAdapter.PostAdapterListener listener;

    private static final int UNIFIED_ADS_VIEW = 1;

    private static final int POST_VIEW = 2;

    public void removeHidedPost(int position){
        postAdsDataList.remove(position);
        notifyItemRangeRemoved(position, postAdsDataList.size());
    }

    public void removeAllPost(){
        if (postAdsDataList != null) {
            postAdsDataList.clear();
            notifyItemRangeRemoved(0, postAdsDataList.size());
        }
    }

    public void removePostCreatedByBlockedUser(String userId){
        List<PostAdsData> shouldRemove = new ArrayList<>();
        for(PostAdsData postAdsData : postAdsDataList){
            if(postAdsData.getPost() != null){
                if(postAdsData.getPost().getAuthorUserId().equals(userId)){
                    shouldRemove.add(postAdsData);
                }
            }
        }
        postAdsDataList.removeAll(shouldRemove);
        notifyDataSetChanged();
    }


    public PostAdapter(PostAdapterListener listener) {
        this.listener = listener;
        setHasStableIds(true);
    }

    public void setPostAdsDataList(List<PostAdsData> postAdsData){
        this.postAdsDataList = postAdsData;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case UNIFIED_ADS_VIEW:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.ad_post_fragment,
                        parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case POST_VIEW:
                // Fall through.
            default:
                if(layoutInflater == null){
                    layoutInflater = LayoutInflater.from(parent.getContext());
                }

                PostItemRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.post_item_row,parent,false);

                return new PostViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        int viewType = getItemViewType(position);

        switch (viewType){
            case UNIFIED_ADS_VIEW:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) postAdsDataList.get(position).getAds();
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) viewHolder).getAdView());
                break;
            case POST_VIEW:
                PostViewHolder holder = (PostViewHolder) viewHolder;

                holder.binding.setData(postAdsDataList.get(position).getPost());

                holder.binding.commentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onCommentClicked(postAdsDataList.get(position).getPost());
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
                        listener.onMoreButtonClicked(postAdsDataList.get(position).getPost(),position);
                    }
                });
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
        if(postAdsDataList.get(position).getType() == 1){
            return UNIFIED_ADS_VIEW;
        }else{
            return POST_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        if (postAdsDataList != null) {
            //Log.d("post SIZE",String.valueOf(post.size()));
            return postAdsDataList.size();
        } else {
            //Log.d("post SIZE","0");
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface PostAdapterListener{
        void onCommentClicked(Post post);

        void onShareClicked(Post post);

        void onMoreButtonClicked(Post post,int position);
    }
}
