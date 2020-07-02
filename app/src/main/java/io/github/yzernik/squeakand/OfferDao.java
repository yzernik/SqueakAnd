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

import io.github.yzernik.squeakand.server.SqueakServerAddress;

@Dao
@TypeConverters({Converters.class})
public interface OfferDao {

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_OFFER + " ORDER BY offerId ASC")
    LiveData<List<Offer>> fetchOffers();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_OFFER  + " WHERE squeakHash = :squeakHash" + " ORDER BY offerId ASC")
    LiveData<List<Offer>> fetchOffersBySqueakHash(Sha256Hash squeakHash);

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_OFFER  + " WHERE squeakServerAddress = :serverAddress" + " ORDER BY offerId ASC")
    LiveData<List<Offer>> fetchOffersByServerAddress(SqueakServerAddress serverAddress);

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_OFFER  + " WHERE squeakHash = :squeakHash AND squeakServerAddress = :serverAddress" + " ORDER BY offerId ASC")
    Offer fetchOfferBySqueakHashAndServerAddress(Sha256Hash squeakHash, SqueakServerAddress serverAddress);

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_OFFER + " WHERE offerId = :offerId")
    LiveData<Offer> fetchLiveOfferById(int offerId);

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_OFFER + " JOIN " + SqueakRoomDatabase.TABLE_NAME_SERVER + " ON offer.squeakServerAddress=server.serverAddress" + " WHERE offerId = :offerId")
    LiveData<OfferWithSqueakServer> fetchLiveOfferWithSqueakServerById(int offerId);

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_OFFER + " WHERE offerId = :offerId")
    Offer fetchOfferById(int offerId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Offer offer);

    @Update
    void update(Offer offer);

    @Delete
    void delete(Offer offer);

    @Query("DELETE FROM " + SqueakRoomDatabase.TABLE_NAME_OFFER)
    void deleteAll();

}
