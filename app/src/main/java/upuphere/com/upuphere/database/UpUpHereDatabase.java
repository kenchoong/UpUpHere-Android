package upuphere.com.upuphere.database;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.NotificationModel;
import upuphere.com.upuphere.models.Post;

import static android.graphics.PorterDuff.Mode.ADD;
import static androidx.room.ColumnInfo.TEXT;

@Database(entities = {AllRooms.class,
                    NotificationModel.class, Post.class}, version = 2,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class UpUpHereDatabase extends RoomDatabase {

    public abstract NotificationDao notificationDao();
    public abstract RoomDao roomDao();
    public abstract PostDao postDao();

    private static volatile UpUpHereDatabase  INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static UpUpHereDatabase  getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UpUpHereDatabase .class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UpUpHereDatabase.class, "UpUpHereDb").addMigrations(FROM_1_TO_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration FROM_1_TO_2 = new Migration(1, 2) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE room_table "
                    + " ADD COLUMN room_owner_user_id TEXT");

            database.execSQL("ALTER TABLE post_table "
                    + " ADD COLUMN author_user_id TEXT");
        }
    };


    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // Populate the database in the background.
                    // If you want to start with more words, just add them.
                    NotificationDao dao = INSTANCE.notificationDao();
                    dao.deleteAll();
                }
            });
        }
    };
}
