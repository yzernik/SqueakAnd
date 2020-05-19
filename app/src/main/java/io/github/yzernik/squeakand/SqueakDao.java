package io.github.yzernik.squeakand;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;


@Dao
@TypeConverters({Converters.class})
public interface SqueakDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from " + TodoRoomDatabase.TABLE_NAME_SQUEAK + " ORDER BY blockHeight DESC")
    LiveData<List<SqueakEntry>> getSqueaks();

    @Query("SELECT * FROM " + TodoRoomDatabase.TABLE_NAME_SQUEAK + " WHERE hash = :squeakHash")
    LiveData<SqueakEntry> fetchSqueakByHash(Sha256Hash squeakHash);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SqueakEntry squeakEntry);

    @Query("DELETE FROM " + TodoRoomDatabase.TABLE_NAME_SQUEAK)
    void deleteAll();
}
