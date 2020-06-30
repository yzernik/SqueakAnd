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
public interface SqueakProfileDao {

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " ORDER BY profile_id ASC")
    LiveData<List<SqueakProfile>> getProfiles();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " WHERE keyPair IS NOT NULL " + " ORDER BY profile_id ASC")
    LiveData<List<SqueakProfile>> getLiveSigningProfiles();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " WHERE uploadEnabled == 1")
    List<SqueakProfile> getProfilesToUpload();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " WHERE downloadEnabled == 1")
    List<SqueakProfile> getProfilesToDownload();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " WHERE keyPair IS NULL " + " ORDER BY profile_id ASC")
    LiveData<List<SqueakProfile>> getContactProfiles();

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " WHERE profile_id = :profileId")
    LiveData<SqueakProfile> fetchProfileById(int profileId);

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " WHERE address = :address")
    LiveData<SqueakProfile> fetchProfileByAddress(String address);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SqueakProfile squeakProfile);

    @Update
    void update(SqueakProfile squeakProfile);

    @Delete
    void delete(SqueakProfile squeakProfile);

    @Query("DELETE FROM " + SqueakRoomDatabase.TABLE_NAME_PROFILE)
    void deleteAll();
}
