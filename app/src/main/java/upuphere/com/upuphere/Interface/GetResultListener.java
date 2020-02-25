package upuphere.com.upuphere.Interface;

import androidx.annotation.Nullable;

public interface GetResultListener {

    //void showError(String error, int blockType, String blockItemId);

    void onHidedItem(String message,int blockType);

    void onBlockedUser(String message,int blockType,String blockUserId);
}
