package io.github.yzernik.squeakand.ui.sendpayment;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
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


    /*    private LiveData<Rpc.Channel> liveInitialOfferChannel() {
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
    }*/


    public LiveData<List<Rpc.Channel>> liveOfferChannel () {
        LiveData<List<Rpc.Channel>> liveChannels = lndRepository.getLiveChannels();
        return Transformations.switchMap(liveChannels, channelsList -> {
            return Transformations.map(liveOffer, offer -> {
                List<Rpc.Channel> offerChannels = new ArrayList<>();
                for (Rpc.Channel channel: channelsList) {
                    Log.i(getClass().getName(), "channel.getRemotePubkey: " + channel.getRemotePubkey());
                    Log.i(getClass().getName(), "offer.pubkey: " + offer.pubkey);
                    if (channel.getRemotePubkey().equals(offer.pubkey)) {
                        Log.i(getClass().getName(), "Found channel for offer: " + channel);
                        //return channel;
                        offerChannels.add(channel);
                    }
                }
                return offerChannels;
            });
        });
    }

    public LiveData<Set<String>> liveConnectedPeers() {
        return lndRepository.liveConnectedPeers();
    }

    public LiveData<Boolean> liveIsOfferPeerConnected() {
        return Transformations.switchMap(liveConnectedPeers(), connectedPeers -> {
            return Transformations.map(liveOffer, offer -> {

                Log.i(getClass().getName(), "Connected peers: " + connectedPeers);
                for (String peer: connectedPeers) {
                    Log.i(getClass().getName(), "Connected peer: " + peer);
                }
                Log.i(getClass().getName(), "offer.pubkey: " + offer.pubkey);

                return connectedPeers.contains(offer.pubkey);
            });
        });
    }


}