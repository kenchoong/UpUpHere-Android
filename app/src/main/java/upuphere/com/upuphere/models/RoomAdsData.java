package upuphere.com.upuphere.models;

import com.google.android.gms.ads.formats.UnifiedNativeAd;

public class RoomAdsData {

    public int type; // 1 for ads , 2 for Room
    public UnifiedNativeAd ads;
    public AllRooms rooms;

    public int getType() {
        return type;
    }

    public UnifiedNativeAd getAds() {
        return ads;
    }

    public AllRooms getRooms() {
        return rooms;
    }
}
