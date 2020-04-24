package upuphere.com.upuphere.app;

public class AppConfig {

    public static String PLATFORM = "Android";

    public static String MEDIA_IS_PHOTO ="111";

    private static String BASE_URL = "http://127.0.0.1:5000";
    //private static String BASE_URL = "http://api.upuphere.com";

    public static String PHOTO_ENDPOINT = BASE_URL +"/media/photos";

    public static String URL_LOGIN = BASE_URL + "/api/auth/login";

    public static String URL_SIGN_UP = BASE_URL + "/api/auth/signup";

    public static String URL_COMPARE_DETAILS = BASE_URL + "/api/auth/compare";

    public static String URL_RESET_PASSWORD = BASE_URL + "/api/auth/reset_password";

    public static String URL_GET_REFRESH_ACCESS_TOKEN = BASE_URL + "/api/auth/token/refresh";

    public static String URL_LOGOUT_ACCESS_TOKEN = BASE_URL + "/api/auth/logout/access";

    public static String URL_LOGOUT_REFRESH_TOKEN = BASE_URL + "/api/auth/logout/refresh";

    public static String URL_GET_ROOM_LIST = BASE_URL + "/api/rooms";

    public static String URL_GET_POST_IN_SPECIFIC_ROOM = BASE_URL + "/api/posts/room/";

    public static String URL_CREATE_ROOM = BASE_URL + "/api/rooms";

    public static String URL_CREATE_POST_TO_ROOM = BASE_URL + "/api/posts/room";

    public static String URL_CREATE_COMMENT = BASE_URL + "/api/comment";

    public static String URL_UPDATE_USER_DETAILS = BASE_URL + "/api/user/update";

    public static String URL_GET_SINGLE_POST = BASE_URL + "/api/posts/";

    public static String URL_BLOCK_UNBLOCK = BASE_URL + "/api/exclude";

    public static String URL_PRIVACY_POLICY = BASE_URL + "/terms/privacy-policy";

    public static String URL_TERM_AND_CONDITION = BASE_URL + "/terms/terms-and-conditions";

    public static String URL_FEEDBACK_FORM = "https://docs.google.com/forms/d/e/1FAIpQLSesbW8xhisMBWOjjG8vCUQlEboXg93Yvgivgxv-3WFpxEsk4A/viewform?usp=sf_link";


    // Notification stuff
    public static final int NOTIFICATION_TEXT = 100;

    public static final String PUSH_NOTIFICATION = "pushNotification";

    // Block user
    public static final int HIDE_POST = 1111;

    public static final int BLOCK_USER = 2222;

    public static final int HIDE_ROOM = 3333;

    public static final int HIDE_COMMENT = 4444;
}
