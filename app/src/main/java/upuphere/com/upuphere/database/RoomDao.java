package upuphere.com.upuphere.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import upuphere.com.upuphere.models.AllRooms;

@Dao
public interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<AllRooms> rooms);

    @Query("DELETE FROM room_table")
    void deleteAll();

    @Query("SELECT * FROM room_table ORDER BY created_at DESC")
    List<AllRooms> getAllRoomInLocalDb();
}
