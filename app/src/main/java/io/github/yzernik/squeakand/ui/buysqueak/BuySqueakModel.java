package io.github.yzernik.squeakand.ui.buysqueak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferRepository;


public class BuySqueakModel extends AndroidViewModel {

    private Sha256Hash squeakHash;
    private LiveData<List<Offer>> mAllOffers;

    private OfferRepository offerRepository;

    public BuySqueakModel(Application application, Sha256Hash squeakHash) {
        super(application);
        this.squeakHash = squeakHash;
        this.offerRepository = OfferRepository.getRepository(application);
        this.mAllOffers = offerRepository.getOffersForSqueak(squeakHash);
    }

    public LiveData<List<Offer>> getOffers() {
        return mAllOffers;
    }

}