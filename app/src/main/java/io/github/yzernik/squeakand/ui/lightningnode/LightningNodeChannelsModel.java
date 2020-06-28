package io.github.yzernik.squeakand.ui.lightningnode;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

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

    private LiveData<List<Rpc.Channel>> listChannels() {
        return lndRepository.getLiveChannels();
    }

    public LiveData<List<Rpc.Channel>> listNodeChannels() {
        return Transformations.map(listChannels(), channels -> {
            return channels.stream()
                    .filter(channel -> channel.getRemotePubkey().equals(pubkey))
                    .collect(Collectors.toList());
        });
    }

    LiveData<Rpc.ClosedChannelUpdate> closeChannel(String channelPoint) {
        return lndRepository.closeChannel(channelPoint, true);
    }

}
