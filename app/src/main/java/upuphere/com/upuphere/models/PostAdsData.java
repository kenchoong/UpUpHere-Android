package upuphere.com.upuphere.models;

import com.google.android.gms.ads.formats.UnifiedNativeAd;

import androidx.annotation.Nullable;

public class PostAdsData {
    public int getType() {
        return type;
    }

    public UnifiedNativeAd getAds() {
        return ads;
    }

    public Post getPost() {
        return post;
    }

    public int type; // 1 is ads and 2 is post
    public UnifiedNativeAd ads;
    public Post post;

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
