package io.github.yzernik.squeakand.ui.sendpayment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.lnd.LndRepository;
import lnrpc.Rpc;

public class SendPaymentModel extends AndroidViewModel {

    private int offerId;
    private SqueakRepository squeakRepository;
    private LndRepository lndRepository;

    private LiveData<Rpc.Channel> liveOfferChannel;

    public SendPaymentModel(Application application, int offerId) {
        super(application);
        this.offerId = offerId;
        this.squeakRepository = SqueakRepository.getRepository(application);
        this.lndRepository = LndRepository.getRepository(application);
        this.liveOfferChannel = new MutableLiveData<>(null);
    }

    public LiveData<Rpc.SendResponse> sendPayment() {
        return squeakRepository.buyOffer(offerId);
    }

    public LiveData<Rpc.ConnectPeerResponse> connectPeer() {
        return squeakRepository.connectPeer(offerId);
    }

    public LiveData<Rpc.ChannelPoint> openChannel() {
        return squeakRepository.openOfferChannel(offerId);
    }

    private void updateOfferChannel() {
        this.liveOfferChannel = squeakRepository.getOfferChannel(offerId);
    }

}