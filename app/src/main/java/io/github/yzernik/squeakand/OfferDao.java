package io.github.yzernik.squeakand;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

@Dao
@TypeConverters({Converters.class})
public interface OfferDao {

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_OFFER + " ORDER BY offerId ASC")
    LiveData<List<Offer>> fetchOffers();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_OFFER  + " WHERE squeakHash = :squeakHash" + " ORDER BY offerId ASC")
    LiveData<List<Offer>> fetchOffersBySqueakHash(Sha256Hash squeakHash);

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_OFFER  + " WHERE squeakHash = :squeakHash AND squeakServerId = :serverId" + " ORDER BY offerId ASC")
    Offer fetchOfferBySqueakHashAndServerId(Sha256Hash squeakHash, int serverId);

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_OFFER + " WHERE offerId = :offerId")
    LiveData<Offer> fetchOfferById(int offerId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Offer offer);

    @Update
    void update(Offer offer);

    @Delete
    void delete(Offer offer);

    @Query("DELETE FROM " + SqueakRoomDatabase.TABLE_NAME_OFFER)
    void deleteAll();

}
