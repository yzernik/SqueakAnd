package io.github.yzernik.squeakand.ui.offer;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferRepository;
import io.github.yzernik.squeakand.OfferWithSqueakServer;
import io.github.yzernik.squeakand.SqueakControllerRepository;
import io.github.yzernik.squeakand.SqueakRepository;
import lnrpc.Rpc;

public class OfferModel extends AndroidViewModel {

    private int offerId;
    private OfferRepository offerRepository;
    private SqueakControllerRepository squeakControllerRepository;

    private LiveData<OfferWithSqueakServer> liveOffer;

    public OfferModel(Application application, int offerId) {
        super(application);
        this.offerId = offerId;
        this.offerRepository = OfferRepository.getRepository(application);
        this.squeakControllerRepository = SqueakControllerRepository.getRepository(application);

        this.liveOffer = offerRepository.getOfferWithSqueakServer(offerId);
    }

    public LiveData<OfferWithSqueakServer> getLiveOffer () {
        return liveOffer;
    }

    public LiveData<DataResult<Rpc.SendResponse>> sendPayment() {
        return squeakControllerRepository.buyOffer(offerId);
    }

}