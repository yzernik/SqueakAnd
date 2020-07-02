package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.yzernik.squeakand.DataResult;
import lnrpc.Rpc;

public class LndRepository {

    private static volatile LndRepository INSTANCE;

    private LndClient lndClient;
    private LndSyncClient lndSyncClient;
    private ExecutorService executorService;
    private LndLiveDataClient lndLiveDataClient;
    private LndEventListener lndEventListener;
    private LndController lndController;

    private LndRepository(Application application) {
        // Singleton constructor, only called by static method.
        this.lndClient = new LndClient();
        this.lndSyncClient = new LndSyncClient();
        this.executorService = Executors.newCachedThreadPool();
        this.lndLiveDataClient = new LndLiveDataClient(lndSyncClient, executorService);
        this.lndEventListener = new LndEventListener(lndClient, lndLiveDataClient, executorService);
        this.lndController = new LndController(application, "testnet", lndLiveDataClient, lndEventListener);
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

    public LiveData<LndWalletStatus> getLndWalletStatus() {
        return lndLiveDataClient.getLndWalletStatus();
    }

    public void waitForWalletUnlocked() throws InterruptedException {
        lndController.waitForWalletUnlocked();
    }

    public void unlockWallet() {
        lndController.unlockWallet();
    }

    public void initWallet() throws InterruptedException {
        lndController.initWallet();
    }

    public String[] getWalletSeedWords() {
        return lndController.getSeedWords();
    }

    public void deleteWallet() {
        lndController.deleteWallet();
    }

    public LiveData<Rpc.GetInfoResponse> getInfo() {
        return lndLiveDataClient.getLiveGetInfo();
    }

    public LiveData<Rpc.WalletBalanceResponse> walletBalance() {
        return lndLiveDataClient.getLiveWalletBalance();
    }

    public LiveData<Rpc.PendingChannelsResponse> pendingChannels() {
        return lndLiveDataClient.getLivePendingChannels();
    }

    public LiveData<Rpc.TransactionDetails> getTransactions() {
        return lndLiveDataClient.getLiveTransactions();
    }

    public LiveData<DataResult<Rpc.NewAddressResponse>> newAddress() {
        return lndLiveDataClient.newAddress();
    }

    public LiveData<DataResult<Rpc.SendResponse>> sendPayment(String paymentRequest) {
        return lndLiveDataClient.sendPayment(paymentRequest);
    }

    public LiveData<DataResult<Rpc.ConnectPeerResponse>> connectPeer(String pubkey, String host) {
        return lndLiveDataClient.connectPeer(pubkey, host);
    }

    public LiveData<Rpc.ListPeersResponse> listPeers() {
        return lndLiveDataClient.getLivePeers();
    }

    public LiveData<DataResult<Rpc.ChannelPoint>> openChannel(String pubkey, long amount) {
        return lndLiveDataClient.openChannel(pubkey, amount);
    }

    public LiveData<DataResult<Rpc.CloseStatusUpdate>> closeChannel(String channelPointString, boolean force) {
        String[] parts = channelPointString.split(":");
        String fundingTx = parts[0];
        int outputIndex = Integer.parseInt(parts[1]);
        Rpc.ChannelPoint channelPoint = Rpc.ChannelPoint.newBuilder()
                .setFundingTxidStr(fundingTx)
                .setOutputIndex(outputIndex)
                .build();
        return closeChannel(channelPoint, force);
    }

    public LiveData<DataResult<Rpc.CloseStatusUpdate>> closeChannel(Rpc.ChannelPoint channelPoint, boolean force) {
        return lndLiveDataClient.closeChannel(channelPoint, force);
    }

    public LiveData<Rpc.ListChannelsResponse> getLiveChannels() {
        return lndLiveDataClient.getLiveChannels();
    }

}
