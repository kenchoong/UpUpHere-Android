package upuphere.com.upuphere.database;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import upuphere.com.upuphere.models.AllRooms;
import upuphere.com.upuphere.models.NotificationModel;

@Database(entities = {AllRooms.class,
                    NotificationModel.class}, version = 1,exportSchema = false)
public abstract class UpUpHereDatabase extends RoomDatabase {

    public abstract NotificationDao notificationDao();
    public abstract RoomDao roomDao();

    private static volatile UpUpHereDatabase  INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static UpUpHereDatabase  getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UpUpHereDatabase .class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UpUpHereDatabase.class, "UpUpHereDb")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

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
