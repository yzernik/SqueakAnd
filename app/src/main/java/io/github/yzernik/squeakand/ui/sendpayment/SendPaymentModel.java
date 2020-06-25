package io.github.yzernik.squeakand.ui.sendpayment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.lnd.LndRepository;
import lnrpc.Rpc;

public class SendPaymentModel extends AndroidViewModel {

    private int offerId;
    private SqueakRepository squeakRepository;
    private LndRepository lndRepository;

    public SendPaymentModel(Application application, int offerId) {
        super(application);
        this.offerId = offerId;
        this.squeakRepository = SqueakRepository.getRepository(application);
        this.lndRepository = LndRepository.getRepository(application);
    }

    public LiveData<Rpc.SendResponse> sendPayment() {
        return squeakRepository.buyOffer(offerId);
    }

    public LiveData<Rpc.ConnectPeerResponse> connectPeer(String pubkey, String host) {
        return lndRepository.connectPeer(pubkey, host);
    }

}