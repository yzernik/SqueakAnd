package io.github.yzernik.squeakand.ui.buysqueak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferRepository;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakServerRepository;
import io.github.yzernik.squeakand.lnd.LndAsyncClient;
import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.server.SqueakNetworkAsyncClient;
import lnrpc.Rpc;


public class BuySqueakModel extends AndroidViewModel {

    private Sha256Hash squeakHash;
    private LiveData<List<Offer>> mAllOffers;

    private OfferRepository offerRepository;
    private SqueakServerRepository squeakServerRepository;
    private LndRepository lndRepository;


    public BuySqueakModel(Application application, Sha256Hash squeakHash) {
        super(application);
        this.squeakHash = squeakHash;
        this.offerRepository = OfferRepository.getRepository(application);
        squeakServerRepository = SqueakServerRepository.getRepository(application);
        this.lndRepository = LndRepository.getRepository(application);
        this.mAllOffers = offerRepository.getOffersForSqueak(squeakHash);
    }

    public Sha256Hash getSqueakHash() {
        return squeakHash;
    }

    public LiveData<List<Offer>> getOffers() {
        return mAllOffers;
    }

    public LiveData<Offer> getBestOffer() {
        return Transformations.map(mAllOffers, offers -> {
            Offer bestOffer = null;
            for (Offer offer: offers) {
                if (bestOffer == null) {
                    bestOffer = offer;
                } else if (offer.amount < bestOffer.amount){
                    bestOffer = offer;
                }
            }
            return bestOffer;
        });
    }

    public SqueakNetworkAsyncClient getAsyncClient() {
        return squeakServerRepository.getSqueakServerAsyncClient();
    }

    public LndAsyncClient getLndAsyncClient() {
        return lndRepository.getLndAsyncClient();
    }

}