package io.github.yzernik.squeakand.ui.lightningnode;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.lnd.LndRepository;
import lnrpc.Rpc;

public class LightningNodeChannelsModel extends AndroidViewModel {

    private String pubkey;
    private LndRepository lndRepository;

    public LightningNodeChannelsModel(@NonNull Application application, String pubkey) {
        super(application);
        this.pubkey = pubkey;
        this.lndRepository = LndRepository.getRepository(application);
    }

    private LiveData<Rpc.ListChannelsResponse> listChannels() {
        return lndRepository.getLiveChannels();
    }

    private LiveData<Rpc.PendingChannelsResponse> pendingChannels() {
        return lndRepository.pendingChannels();
    }

    public LiveData<List<Rpc.Channel>> listNodeChannels() {
        return Transformations.map(listChannels(), response -> {
            List<Rpc.Channel> channels = response.getChannelsList();
            return channels.stream()
                    .filter(channel -> channel.getRemotePubkey().equals(pubkey))
                    .collect(Collectors.toList());
        });
    }

    public LiveData<List<Rpc.PendingChannelsResponse.PendingOpenChannel>> pendingNodeChannels() {
        return Transformations.map(pendingChannels(), response -> {
            List<Rpc.PendingChannelsResponse.PendingOpenChannel> pendingOpenChannels = response.getPendingOpenChannelsList();
            return pendingOpenChannels.stream()
                    .filter(channel -> channel.getChannel().getRemoteNodePub().equals(pubkey))
                    .collect(Collectors.toList());
        });
    }

    LiveData<DataResult<Rpc.CloseStatusUpdate>> closeChannel(String channelPoint) {
        return lndRepository.closeChannel(channelPoint, false);
    }

}
