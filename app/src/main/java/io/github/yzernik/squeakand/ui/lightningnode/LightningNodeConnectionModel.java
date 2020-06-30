package io.github.yzernik.squeakand.ui.lightningnode;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.DataResult;
import lnrpc.Rpc;

public class LightningNodeConnectionModel extends AndroidViewModel {

    private final String pubkey;
    @Nullable
    private final String host;
    private final LndRepository lndRepository;

    public LightningNodeConnectionModel(@NonNull Application application, String pubkey, String host) {
        super(application);
        this.pubkey = pubkey;
        this.host = host;
        this.lndRepository = LndRepository.getRepository(application);
    }

    public String getPubkey() {
        return pubkey;
    }

    public String getHost() {
        return host;
    }

    private LiveData<Set<String>> liveConnectedPeers() {
        return lndRepository.liveConnectedPeers();
    }

    public LiveData<Boolean> liveIsPeerConnected() {
        return Transformations.map(liveConnectedPeers(), connectedPeers -> {
            return connectedPeers.contains(pubkey);
        });
    }

    public LiveData<DataResult<Rpc.ConnectPeerResponse>> connectToPeer() {
        return lndRepository.connectPeer(pubkey, host);
    }

    public LiveData<DataResult<Rpc.ChannelPoint>> openChannel(long amount) {
        return lndRepository.openChannel(pubkey, amount);
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

    public LiveData<Long> liveConfirmedBalance() {
        LiveData<DataResult<Rpc.WalletBalanceResponse>> liveWalletBalance = lndRepository.walletBalance();
        return Transformations.map(liveWalletBalance, walletBalance -> {
            if (!walletBalance.isSuccess()) {
                return null;
            }
            return walletBalance.getResponse().getConfirmedBalance();
        });
    }

}
