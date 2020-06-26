package io.github.yzernik.squeakand;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

public class OfferRepository {

    private static volatile OfferRepository INSTANCE;


    private OfferDao mOfferDao;

    // Note that in order to unit test the TodoRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples

    private OfferRepository(Application application) {
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        mOfferDao = db.offerDao();
    }

    public static OfferRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (OfferRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OfferRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<Offer>> getOffersForSqueak(Sha256Hash squeakHash) {
        return mOfferDao.fetchOffersBySqueakHash(squeakHash);
    }

    public LiveData<Offer> getOffer(int offerId) {
        return mOfferDao.fetchLiveOfferById(offerId);
    }

}
