package io.github.yzernik.squeakand;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SqueakServerDao {

    String getServersQuery = "SELECT * from " + SqueakRoomDatabase.TABLE_NAME_SERVER;

    @Query(getServersQuery)
    LiveData<List<SqueakServer>> getLiveServers();

    @Query(getServersQuery)
    List<SqueakServer> getServers();

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_SERVER + " WHERE server_id = :serverId")
    LiveData<SqueakServer> fetchServerById(int serverId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SqueakServer squeakServer);

    @Update
    void update(SqueakServer squeakServer);

    @Delete
    void delete(SqueakServer squeakServer);

    @Query("DELETE FROM " + SqueakRoomDatabase.TABLE_NAME_SERVER)
    void deleteAll();
}
