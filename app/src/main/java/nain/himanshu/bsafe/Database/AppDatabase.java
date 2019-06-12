package nain.himanshu.bsafe.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities =  {Contacts.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "bsafe.db";
    private static volatile AppDatabase appDatabase;

    public static synchronized AppDatabase getInstance(Context context){
        if (appDatabase == null) appDatabase = create(context);
        return appDatabase;
    }

    private static AppDatabase create(final Context context){
        return Room.databaseBuilder(context,AppDatabase.class,DB_NAME).fallbackToDestructiveMigration().build();
    }

    public abstract ContactsDao contactsDao();

}
