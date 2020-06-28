package io.github.yzernik.squeakand.ui.sendpayment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferRepository;
import io.github.yzernik.squeakand.SqueakRepository;
import lnrpc.Rpc;

public class SendPaymentModel extends AndroidViewModel {

    private int offerId;
    private OfferRepository offerRepository;
    private SqueakRepository squeakRepository;

    private LiveData<Offer> liveOffer;

    public SendPaymentModel(Application application, int offerId) {
        super(application);
        this.offerId = offerId;
        this.offerRepository = OfferRepository.getRepository(application);
        this.squeakRepository = SqueakRepository.getRepository(application);

        this.liveOffer = offerRepository.getOffer(offerId);
    }

    public LiveData<Offer> getLiveOffer () {
        return liveOffer;
    }

    public LiveData<Rpc.SendResponse> sendPayment() {
        return squeakRepository.buyOffer(offerId);
    }

}