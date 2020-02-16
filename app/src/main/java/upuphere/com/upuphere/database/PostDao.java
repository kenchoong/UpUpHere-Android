package upuphere.com.upuphere.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import upuphere.com.upuphere.models.Post;

@Dao
public interface PostDao {

    @Query("SELECT * FROM post_table WHERE in_room = :roomId")
    List<Post> getPostByRoomIdInLocalDb(String roomId);

    @Query("SELECT * FROM post_table WHERE id = :postId")
    List<Post> getPostByPostIdInLocalDb(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Post> postList);

    @Query("DELETE FROM post_table")
    void deleteAll();
}
