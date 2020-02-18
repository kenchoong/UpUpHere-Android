package upuphere.com.upuphere.models;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    public UserModel(String userId,String username, String userSessionId, String userDeviceId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.username = username;
        this.userSessionId = userSessionId;
        this.userDeviceId = userDeviceId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @SerializedName("user_id")
    private String userId;

    @SerializedName("username")
    private String username;

    @SerializedName("user_session_id")
    private String userSessionId;

    @SerializedName("user_device_id")
    private String userDeviceId;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(String userSessionId) {
        this.userSessionId = userSessionId;
    }

    public String getUserDeviceId() {
        return userDeviceId;
    }

    public void setUserDeviceId(String userDeviceId) {
        this.userDeviceId = userDeviceId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


}
