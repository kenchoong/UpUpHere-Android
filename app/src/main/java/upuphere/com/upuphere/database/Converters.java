package upuphere.com.upuphere.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;
import upuphere.com.upuphere.models.CommentModel;

public class Converters {

    @TypeConverter
    public static List<CommentModel> restoreCommentListFromString(String commentString) {
        if(commentString == null){
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CommentModel>>(){}.getType();
        return gson.fromJson(commentString,type);
    }

    @TypeConverter
    public static String saveCommentListAsString(List<CommentModel> commentList) {
        if(commentList == null){
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CommentModel>>(){}.getType();
        return gson.toJson(commentList,type);
    }
}
