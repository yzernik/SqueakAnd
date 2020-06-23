package io.github.yzernik.squeakand.ui.buysqueak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferRepository;
import io.github.yzernik.squeakand.SqueakServerRepository;
import io.github.yzernik.squeakand.server.SqueakServerAsyncClient;


public class BuySqueakModel extends AndroidViewModel {

    private Sha256Hash squeakHash;
    private LiveData<List<Offer>> mAllOffers;

    private OfferRepository offerRepository;
    private SqueakServerRepository squeakServerRepository;


    public BuySqueakModel(Application application, Sha256Hash squeakHash) {
        super(application);
        this.squeakHash = squeakHash;
        this.offerRepository = OfferRepository.getRepository(application);
        squeakServerRepository = SqueakServerRepository.getRepository(application);
        this.mAllOffers = offerRepository.getOffersForSqueak(squeakHash);
    }

    public Sha256Hash getSqueakHash() {
        return squeakHash;
    }

    public LiveData<List<Offer>> getOffers() {
        return mAllOffers;
    }

    public SqueakServerAsyncClient getAsyncClient() {
        return squeakServerRepository.getSqueakServerAsyncClient();
    }

}