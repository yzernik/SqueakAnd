package io.github.yzernik.squeakand.ui.sendpayment;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.Set;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferRepository;
import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.lnd.LndRepository;
import lnrpc.Rpc;

public class SendPaymentModel extends AndroidViewModel {

    private int offerId;
    private OfferRepository offerRepository;
    private SqueakRepository squeakRepository;
    private LndRepository lndRepository;

    private LiveData<Offer> liveOffer;
    private LiveData<Rpc.Channel> liveOfferChannel;

    public SendPaymentModel(Application application, int offerId) {
        super(application);
        this.offerId = offerId;
        this.offerRepository = OfferRepository.getRepository(application);
        this.squeakRepository = SqueakRepository.getRepository(application);
        this.lndRepository = LndRepository.getRepository(application);

        this.liveOffer = offerRepository.getOffer(offerId);
    }

    public LiveData<Offer> getLiveOffer () {
        return liveOffer;
    }

    public LiveData<Rpc.SendResponse> sendPayment() {
        return squeakRepository.buyOffer(offerId);
    }

    public LiveData<Rpc.ConnectPeerResponse> connectPeer() {
        return squeakRepository.connectPeer(offerId);
    }

    public LiveData<Rpc.ChannelPoint> openChannel(long amount) {
        return squeakRepository.openOfferChannel(offerId, amount);
    }

    private LiveData<Rpc.Channel> liveInitialOfferChannel() {
        LiveData<Rpc.ListChannelsResponse> liveChannelsList = lndRepository.listChannels();
        return Transformations.switchMap(liveChannelsList, channelsList -> {
            return Transformations.map(liveOffer, offer -> {
                for (Rpc.Channel channel: channelsList.getChannelsList()) {
                    Log.i(getClass().getName(), "channel.getRemotePubkey: " + channel.getRemotePubkey());
                    Log.i(getClass().getName(), "offer.pubkey: " + offer.pubkey);
                    if (channel.getRemotePubkey().equals(offer.pubkey)) {
                        Log.i(getClass().getName(), "Found channel for offer: " + channel);
                        return channel;
                    }
                }
                return null;
            });
        });
    }

    private LiveData<Rpc.Channel> liveSubscribedOfferChannel () {
        LiveData<Rpc.ChannelEventUpdate> liveChannelEventUpdate = lndRepository.subscribeChannelEvents();
        return Transformations.switchMap(liveChannelEventUpdate, update -> {
            return Transformations.map(liveOffer, offer -> {
                Rpc.Channel openChannel = update.getOpenChannel();
                if (openChannel.getRemotePubkey().equals(offer.pubkey)) {
                    Log.i(getClass().getName(), "Found opened channel for offer: " + openChannel);
                    return openChannel;
                }

                return null;
            });
        });
    }

    // TODO: move this logic to repository, use pubkey as parameter.
    public LiveData<Rpc.Channel> liveOfferChannel () {
/*        return Transformations.switchMap(liveInitialOfferChannel(), initialOfferChannel -> {
            return Transformations.map(liveSubscribedOfferChannel(), subscribedOfferChannel -> {

                Log.i(getClass().getName(), "Inside transformation (initialOfferChannel): " + initialOfferChannel);
                Log.i(getClass().getName(), "Inside transformation (subscribedOfferChannel): " + subscribedOfferChannel);

                if (subscribedOfferChannel != null) {
                    return subscribedOfferChannel;
                }
                return initialOfferChannel;
            });
        });*/

        return liveInitialOfferChannel();
    }

    public LiveData<Set<String>> liveConnectedPeers() {
        return lndRepository.liveConnectedPeers();
    }



}