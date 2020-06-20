package io.github.yzernik.squeakand;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;


@Dao
@TypeConverters({Converters.class})
public interface SqueakDao {

    String fetchSqueaksByAddressQuery = "SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " WHERE authorAddress = :address";

    String fetchSqueakWithProfileByHashQuery = "SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " LEFT JOIN " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " ON squeak.authorAddress=profile.address" + " WHERE hash = :squeakHash";

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " ORDER BY blockHeight DESC, time DESC")
    LiveData<List<SqueakEntry>> getSqueaks();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " LEFT JOIN " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " ON squeak.authorAddress=profile.address" + " ORDER BY blockHeight DESC, time DESC, decryptedContentStr DESC")
    LiveData<List<SqueakEntryWithProfile>> getSqueaksWithProfile();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " JOIN " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " ON squeak.authorAddress=profile.address" + " WHERE squeak.block IS NOT NULL AND profile.downloadEnabled=1" + " ORDER BY blockHeight DESC, time DESC, decryptedContentStr DESC")
    LiveData<List<SqueakEntryWithProfile>> getTimelineSqueaksWithProfile();

    @Query(fetchSqueakWithProfileByHashQuery)
    LiveData<SqueakEntryWithProfile> fetchLiveSqueakByHash(Sha256Hash squeakHash);

    @Query("WITH RECURSIVE\n" +
            "  is_thread_ancestor(n) AS (\n" +
            "    VALUES(:squeakHash)\n" +
            "    UNION\n" +
            "    SELECT hashReplySqk FROM squeak, is_thread_ancestor\n" +
            "     WHERE squeak.hash=is_thread_ancestor.n\n" +
            "  )\n" +
            "SELECT * FROM squeak\n" +
            " WHERE squeak.hash IN is_thread_ancestor;")
    LiveData<List<SqueakEntry>> fetchLiveSqueakReplyAncestorsByHash(Sha256Hash squeakHash);

    @Query(fetchSqueakWithProfileByHashQuery)
    SqueakEntryWithProfile fetchSqueakWithProfileByHash(Sha256Hash squeakHash);

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " WHERE hash = :squeakHash")
    SqueakEntry fetchSqueakByHash(Sha256Hash squeakHash);

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " LEFT JOIN " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " ON squeak.authorAddress=profile.address" + " WHERE squeak.authorAddress = :address AND squeak.block IS NOT NULL" + " ORDER BY blockHeight DESC, time DESC, decryptedContentStr DESC")
    LiveData<List<SqueakEntryWithProfile>> fetchLiveSqueaksByAddress(String address);

    @Query(fetchSqueaksByAddressQuery)
    List<SqueakEntry> fetchSqueaksByAddress(String address);

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_SQUEAK + " WHERE block IS NULL")
    List<SqueakEntry> fetchUnverifiedSqueaks();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SqueakEntry squeakEntry);

    @Update
    void update(SqueakEntry squeakEntry);

    @Query("DELETE FROM " + SqueakRoomDatabase.TABLE_NAME_SQUEAK)
    void deleteAll();
}
