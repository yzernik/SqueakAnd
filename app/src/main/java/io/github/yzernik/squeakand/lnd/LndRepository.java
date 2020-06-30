package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lnrpc.Rpc;

public class LndRepository {

    private static volatile LndRepository INSTANCE;

    // private LndClient lndClient;
    private LndSyncClient lndSyncClient;
    private ExecutorService executorService;
    private LndLiveDataClient lndLiveDataClient;
    private LndController lndController;

    private LndRepository(Application application) {
        // Singleton constructor, only called by static method.
        this.lndSyncClient = new LndSyncClient();
        this.executorService = Executors.newCachedThreadPool();
        this.lndLiveDataClient = new LndLiveDataClient(lndSyncClient, executorService);
        this.lndController = new LndController(application, "testnet");
    }

    public static LndRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (LndRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LndRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public LndSyncClient getLndSyncClient() {
        return lndSyncClient;
    }

    public void initialize() {
        Log.i(getClass().getName(), "LndRepository: Calling initialize ...");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                lndController.initialize();
            }
        });
    }

    public LiveData<LndResult<Rpc.GetInfoResponse>> getInfo() {
        return lndLiveDataClient.getInfo();
    }

    public LiveData<LndResult<Rpc.WalletBalanceResponse>> walletBalance() {
        return lndLiveDataClient.walletBalance();
    }

    public LiveData<LndResult<Rpc.ListChannelsResponse>> listChannels() {
        return lndLiveDataClient.listChannels();
    }

    public LiveData<LndResult<Rpc.NewAddressResponse>> newAddress() {
        return lndLiveDataClient.newAddress();
    }

    public LiveData<LndResult<Rpc.SendResponse>> sendPayment(String paymentRequest) {
        return lndLiveDataClient.sendPayment(paymentRequest);
    }

    public LiveData<LndResult<Rpc.ConnectPeerResponse>> connectPeer(String pubkey, String host) {
        return lndLiveDataClient.connectPeer(pubkey, host);
    }

    public LiveData<LndResult<Rpc.ListPeersResponse>> listPeers() {
        return lndLiveDataClient.listPeers();
    }

    public LiveData<LndResult<Rpc.ChannelPoint>> openChannel(String pubkey, long amount) {
        return lndLiveDataClient.openChannel(pubkey, amount);
    }

    public LiveData<Rpc.ClosedChannelUpdate> closeChannel(String channelPointString, boolean force) {
        String[] parts = channelPointString.split(":");
        String fundingTx = parts[0];
        int outputIndex = Integer.parseInt(parts[1]);
        Rpc.ChannelPoint channelPoint = Rpc.ChannelPoint.newBuilder()
                .setFundingTxidStr(fundingTx)
                .setOutputIndex(outputIndex)
                .build();
        return closeChannel(channelPoint, force);
    }

    public LiveData<Rpc.ClosedChannelUpdate> closeChannel(Rpc.ChannelPoint channelPoint, boolean force) {
        return lndLiveDataClient.closeChannel(channelPoint, force);
    }

    public LiveData<Set<String>> liveConnectedPeers() {
        return lndLiveDataClient.liveConnectedPeers();
    }

    public LiveData<List<Rpc.Channel>> getLiveChannels() {
        return lndLiveDataClient.getLiveChannels();
    }

}
