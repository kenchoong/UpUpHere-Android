package upuphere.com.upuphere.app;

public class AppConfig {

    public static String PLATFORM = "Android";

    private static String BASE_URL = "http://127.0.0.1:5000";

    public static String PHOTO_ENDPOINT = BASE_URL +"/media/photos";

    public static String URL_LOGIN = BASE_URL + "/api/auth/login";

    public static String URL_SIGN_UP = BASE_URL + "/api/auth/signup";

    public static String URL_COMPARE_DETAILS = BASE_URL + "/api/auth/compare";

    public static String URL_GET_REFRESH_ACCESS_TOKEN = BASE_URL + "/api/auth/token/refresh";

    public static String URL_LOGOUT_ACCESS_TOKEN = BASE_URL + "/api/auth/logout/access";

    public static String URL_LOGOUT_REFRESH_TOKEN = BASE_URL + "/api/auth/logout/refresh";

    public static String URL_GET_ROOM_LIST = BASE_URL + "/api/rooms";

    public static String URL_GET_POST_IN_SPECIFIC_ROOM = BASE_URL + "/api/posts/room/";

    public static String URL_CREATE_ROOM = BASE_URL + "/api/rooms";
}
