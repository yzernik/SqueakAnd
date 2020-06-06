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

    String fetchSqueaksByAddressQuery = "SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " WHERE authorAddress = :address";

    String fetchSqueakByHashQuery = "SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " WHERE hash = :squeakHash";

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " ORDER BY blockHeight DESC, time DESC")
    LiveData<List<SqueakEntry>> getSqueaks();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " LEFT JOIN " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " ON squeak.authorAddress=profile.address" + " ORDER BY blockHeight DESC, time DESC, decryptedContentStr DESC")
    LiveData<List<SqueakEntryWithProfile>> getSqueaksWithProfile();

    @Query(fetchSqueakByHashQuery)
    LiveData<SqueakEntry> fetchLiveSqueakByHash(Sha256Hash squeakHash);

    @Query(fetchSqueakByHashQuery)
    SqueakEntry fetchSqueakByHash(Sha256Hash squeakHash);

    @Query(fetchSqueaksByAddressQuery)
    LiveData<List<SqueakEntry>> fetchLiveSqueaksByAddress(String address);

    @Query(fetchSqueaksByAddressQuery)
    List<SqueakEntry> fetchSqueaksByAddress(String address);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SqueakEntry squeakEntry);

    @Query("DELETE FROM " + SqueakRoomDatabase.TABLE_NAME_SQUEAK)
    void deleteAll();
}
