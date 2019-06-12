package nain.himanshu.bsafe.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactsDao {

    @Query("SELECT * FROM Contacts")
    List<Contacts> getAll();

    @Insert
    void insert(Contacts contact);

    @Delete
    void delete(Contacts contact);

    @Query("DELETE FROM Contacts")
    void deleteAll();

    @Query("SELECT number FROM Contacts")
    List<String> getAllNumbers();
}
