package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.yzernik.squeakand.preferences.Preferences;
import lnrpc.Rpc;
import lnrpc.Walletunlocker;

public class LndController {

    private static final long START_TIMEOUT_S = 30;
    private static final long STOP_TIMEOUT_S = 10;
    private static final long UNLOCK_TIMEOUT_S = 10;
    private static final long GEN_SEED_TIMEOUT_S = 10;
    private static final long INIT_WALLET_TIMEOUT_S = 10;
    private static final long GET_INFO_TIMEOUT_S = 10;
    private static final long WALLET_BALANCE_TIMEOUT_S = 10;
    private static final long LIST_CHANNELS_TIMEOUT_S = 10;
    private static final long NEW_ADDRESS_TIMEOUT_S = 10;
    private static final long SEND_PAYMENT_TIMEOUT_S = 10;
    private static final long CONNECT_PEER_TIMEOUT_S = 10;
    private static final long LIST_PEERS_TIMEOUT_S = 10;
    private static final long OPEN_CHANNEL_TIMEOUT_S = 10;


    private static final String LND_DIR_RELATIVE_PATH = "/.lnd";

    // TODO: Use a real password.
    private static final String DEFAULT_PASSWORD = "somesuperstrongpw";

    private final String lndDir;
    private final String network;
    private final String password;
    private final LndClient lndClient;
    private final Preferences preferences;

    public LndController(Application application, String network, String password) {
        this.lndDir = Paths.get(application.getFilesDir().toString(), LND_DIR_RELATIVE_PATH).toString();
        this.network = network;
        this.password = password;
        this.lndClient = new LndClient();
        this.preferences = new Preferences(application);
    }

    public LndController(Application application, String network) {
        this(application, network, DEFAULT_PASSWORD);
    }

    public LndClient getLndClient() {
        return lndClient;
    }

    /**
     * Start the lnd node.
     */
    public String start() throws InterruptedException, ExecutionException, TimeoutException {
        Future<String> startResultFuture = StartWalletTask.startWallet(lndClient, lndDir, network);
        return startResultFuture.get(START_TIMEOUT_S, TimeUnit.SECONDS);
    }

    /**
     * Start the lnd node.
     */
    public Rpc.StopResponse stop() throws InterruptedException, ExecutionException, TimeoutException {
        Future<Rpc.StopResponse> stopResultFuture = StopDaemonTask.stopDaemon(lndClient);
        return stopResultFuture.get(STOP_TIMEOUT_S, TimeUnit.SECONDS);
    }

    /*
    public void genSeed() {
        lndClient.genSeed(new LndClient.GenSeedCallBack() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error calling genSeed: " + e);
            }

            @Override
            public void onResponse(Walletunlocker.GenSeedResponse response) {
                List<String> seedWordsList = response.getCipherSeedMnemonicList();
                String[] seedWords = new String[seedWordsList.size()];
                seedWordsList.toArray(seedWords);
                preferences.saveWalletSeed(seedWords);
            }
        });
    }*/

    /**
     * Initialize the wallet using a new generated seed.
     */
    public void initWallet() {
        lndClient.genSeed(new LndClient.GenSeedCallBack() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error calling genSeed: " + e);
            }

            @Override
            public void onResponse(Walletunlocker.GenSeedResponse response) {
                List<String> seedWordsList = response.getCipherSeedMnemonicList();
                String[] seedWords = new String[seedWordsList.size()];
                seedWordsList.toArray(seedWords);
                preferences.saveWalletSeed(seedWords);

                Log.i(getClass().getName(), "Initializing wallet with new seed: " + seedWordsList);
                lndClient.initWallet(password, seedWordsList, new LndClient.InitWalletCallBack() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(getClass().getName(), "Error calling initWallet: " + e);
                    }

                    @Override
                    public void onResponse(Walletunlocker.InitWalletResponse response) {
                        Log.i(getClass().getName(), "Got initWallet response.");
                    }
                });
            }
        });
    }

    public Walletunlocker.UnlockWalletResponse unlockWallet() throws InterruptedException, ExecutionException, TimeoutException {
        Future<Walletunlocker.UnlockWalletResponse> unlockResultFuture = UnlockWalletTask.unlockWallet(lndClient, password);
        return unlockResultFuture.get(WALLET_BALANCE_TIMEOUT_S, TimeUnit.SECONDS);
    }

    public String[] genSeed() throws InterruptedException, ExecutionException, TimeoutException {
        Future<Walletunlocker.GenSeedResponse> genSeedResultFuture = GenSeedTask.genSeed(lndClient);
        Walletunlocker.GenSeedResponse response = genSeedResultFuture.get(GEN_SEED_TIMEOUT_S, TimeUnit.SECONDS);
        List<String> seedWordsList = response.getCipherSeedMnemonicList();
        String[] seedWords = new String[seedWordsList.size()];
        seedWordsList.toArray(seedWords);
        return seedWords;

        // TODO: Only save the seed words after completing initWallet.

        /*        List<String> seedWordsList = response.getCipherSeedMnemonicList();
        String[] seedWords = new String[seedWordsList.size()];
        seedWordsList.toArray(seedWords);
        preferences.saveWalletSeed(seedWords);*/
    }

    public Walletunlocker.InitWalletResponse initWallet(String[] seedWords) throws InterruptedException, ExecutionException, TimeoutException {
        List<String> seedWordsList = Arrays.asList(seedWords);
        Future<Walletunlocker.InitWalletResponse> initWalletResultFuture = InitWalletTask.initWallet(lndClient, password, seedWordsList);
        Walletunlocker.InitWalletResponse response = initWalletResultFuture.get(INIT_WALLET_TIMEOUT_S, TimeUnit.SECONDS);
        // Save the seed words after completing initWallet.
        preferences.saveWalletSeed(seedWords);
        return response;
    }

    public void rmLndDir() {
        Path lndDirPath = Paths.get(lndDir);
        Log.i(getClass().getName(), "Deleting lnd dir: " + lndDirPath);
        deleteDirectory(lndDirPath.toFile());
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    /**
     * Get info async.
     */
    public Future<Rpc.GetInfoResponse> getInfoAsync() {
        return GetInfoTask.getInfo(lndClient);
    }

    /**
     * Get info.
     */
    public Rpc.GetInfoResponse getInfo() throws InterruptedException, ExecutionException, TimeoutException {
        Future<Rpc.GetInfoResponse> getInfoResultFuture = getInfoAsync();
        return getInfoResultFuture.get(GET_INFO_TIMEOUT_S, TimeUnit.SECONDS);
    }

    /**
     * Wallet balance async.
     */
    public Future<Rpc.WalletBalanceResponse> walletBalanceAsync() {
        return WalletBalanceTask.walletBalance(lndClient);
    }

    /**
     * Wallet balance.
     */
    public Rpc.WalletBalanceResponse walletBalance() throws InterruptedException, ExecutionException, TimeoutException {
        Future<Rpc.WalletBalanceResponse> walletBalanceResultFuture = walletBalanceAsync();
        return walletBalanceResultFuture.get(GET_INFO_TIMEOUT_S, TimeUnit.SECONDS);
    }

    /**
     * List channels async.
     */
    public Future<Rpc.ListChannelsResponse> listChannelsAsync() {
        return ListChannelsTask.listChannels(lndClient);
    }

    /**
     * List channels.
     */
    public Rpc.ListChannelsResponse listChannels() throws InterruptedException, ExecutionException, TimeoutException {
        Future<Rpc.ListChannelsResponse> listChannelsResultFuture = listChannelsAsync();
        return listChannelsResultFuture.get(LIST_CHANNELS_TIMEOUT_S, TimeUnit.SECONDS);
    }

    /**
     * New address async.
     */
    public Future<Rpc.NewAddressResponse> newAddressAsync() {
        return NewAddressTask.newAddress(lndClient);
    }

    /**
     * New address.
     */
    public Rpc.NewAddressResponse newAddress() throws InterruptedException, ExecutionException, TimeoutException {
        Future<Rpc.NewAddressResponse> newAddressResultFuture = newAddressAsync();
        return newAddressResultFuture.get(NEW_ADDRESS_TIMEOUT_S, TimeUnit.SECONDS);
    }

    /**
     * Send payment async.
     */
    public Future<Rpc.SendResponse> sendPaymentAsync(String paymentRequest) {
        return SendPaymentTask.sendPayment(paymentRequest, lndClient);
    }

    /**
     * Send payment.
     */
    public Rpc.SendResponse sendPayment(String paymentRequest) throws InterruptedException, ExecutionException, TimeoutException {
        Future<Rpc.SendResponse> sendPaymentResultFuture = sendPaymentAsync(paymentRequest);
        return sendPaymentResultFuture.get(SEND_PAYMENT_TIMEOUT_S, TimeUnit.SECONDS);
    }

    public LndResult<Rpc.SendResponse> sendPaymentWithResult(String paymentRequest) {
        try {
            Rpc.SendResponse response = sendPayment(paymentRequest);
            return LndResult.ofSuccess(response);
        } catch (TimeoutException | InterruptedException e) {
            return LndResult.ofFailure(e);
        } catch (ExecutionException e) {
            return LndResult.ofFailure(e.getCause());
        }
    }

    /**
     * Connect peer async.
     */
    public Future<Rpc.ConnectPeerResponse> connectPeerAsync(String pubkey, String host) {
        return ConnectPeerTask.connectPeer(pubkey, host, lndClient);
    }

    /**
     * Connect peer.
     */
    public Rpc.ConnectPeerResponse connectPeer(String pubkey, String host) throws InterruptedException, ExecutionException, TimeoutException {
        Future<Rpc.ConnectPeerResponse> connectPeerResultFuture = connectPeerAsync(pubkey, host);
        return connectPeerResultFuture.get(CONNECT_PEER_TIMEOUT_S, TimeUnit.SECONDS);
    }

    /**
     * Connect peer with result.
     * @param pubkey
     * @param host
     * @return
     */
    public LndResult<Rpc.ConnectPeerResponse> connectPeerWithResult(String pubkey, String host) {
        try {
            Rpc.ConnectPeerResponse response = connectPeer(pubkey, host);
            return LndResult.ofSuccess(response);
        } catch (TimeoutException | InterruptedException e) {
            return LndResult.ofFailure(e);
        } catch (ExecutionException e) {
            return LndResult.ofFailure(e.getCause());
        }
    }

    /**
     * List peers async.
     */
    public Future<Rpc.ListPeersResponse> listPeersAsync() {
        return ListPeersTask.listPeers(lndClient);
    }

    /**
     * List peers.
     */
    public Rpc.ListPeersResponse listPeers() throws InterruptedException, ExecutionException, TimeoutException {
        Future<Rpc.ListPeersResponse> listPeersResultFuture = listPeersAsync();
        return listPeersResultFuture.get(LIST_PEERS_TIMEOUT_S, TimeUnit.SECONDS);
    }

    /**
     * Open channel async.
     */
    public Future<Rpc.ChannelPoint> openChannelAsync(String pubkey, long amount) {
        return OpenChannelTask.openChannel(pubkey, amount, lndClient);
    }

    /**
     * Open channel.
     */
    public Rpc.ChannelPoint openChannel(String pubkey, long amount) throws InterruptedException, ExecutionException, TimeoutException {
        Log.i(getClass().getName(), "Openning channel with pubkey: " + pubkey + ", funding amount: " + amount);
        Future<Rpc.ChannelPoint> openChannelResultFuture = openChannelAsync(pubkey, amount);
        return openChannelResultFuture.get(OPEN_CHANNEL_TIMEOUT_S, TimeUnit.SECONDS);
    }

    /**
     * Open channel with result.
     * @param pubkey
     * @param amount
     * @return
     */
    public LndResult<Rpc.ChannelPoint> openChannelWithResult(String pubkey, long amount) {
        try {
            Rpc.ChannelPoint response = openChannel(pubkey, amount);
            return LndResult.ofSuccess(response);
        } catch (TimeoutException | InterruptedException e) {
            return LndResult.ofFailure(e);
        } catch (ExecutionException e) {
            return LndResult.ofFailure(e.getCause());
        }
    }

    /**
     * Open channel async.
     */
    public void subscribeChannelEvents(LndClient.SubscribeChannelEventsRecvStream recvStream) {
        lndClient.subscribeChannelEvents(recvStream);
    }

    /**
     * Close channel.
     */
    public void closeChannel(Rpc.ChannelPoint channelPoint, boolean force, LndClient.CloseChannelEventsRecvStream recvStream) {
        lndClient.closeChannel(channelPoint, force, recvStream);
    }

    /**
     * Subscribe peer events.
     */
    public void subscribePeerEvents(LndClient.SubscribePeerEventsRecvStream recvStream) {
        lndClient.subscribePeerEvents(recvStream);
    }

}
