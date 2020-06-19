package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.yzernik.squeakand.preferences.Preferences;
import lnrpc.Rpc;
import lnrpc.Walletunlocker;

public class LndController {

    private static final long START_TIMEOUT_S = 10;
    private static final long STOP_TIMEOUT_S = 30;
    private static final long UNLOCK_TIMEOUT_S = 10;


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
    }

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
        return unlockResultFuture.get(UNLOCK_TIMEOUT_S, TimeUnit.SECONDS);
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
     * Get info.
     */
    public LiveData<Rpc.GetInfoResponse> getInfo() {
        MutableLiveData<Rpc.GetInfoResponse> liveDataResponse = new MutableLiveData<>(null);

        lndClient.getInfo(new LndClient.GetInfoCallBack() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error calling getInfo: " + e);
            }

            @Override
            public void onResponse(Rpc.GetInfoResponse response) {
                liveDataResponse.postValue(response);
            }
        });

        return liveDataResponse;
    }

    /**
     * Wallet balance.
     */
    public LiveData<Rpc.WalletBalanceResponse> walletBalance() {
        MutableLiveData<Rpc.WalletBalanceResponse> liveDataResponse = new MutableLiveData<>(null);

        lndClient.walletBalance(new LndClient.WalletBalanceCallBack() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error calling walletBalance: " + e);
            }

            @Override
            public void onResponse(Rpc.WalletBalanceResponse response) {
                liveDataResponse.postValue(response);
            }
        });

        return liveDataResponse;
    }


    /**
     * List channels.
     */
    public LiveData<Rpc.ListChannelsResponse> listChannels() {
        MutableLiveData<Rpc.ListChannelsResponse> liveDataResponse = new MutableLiveData<>(null);

        lndClient.listChannels(new LndClient.ListChannelsCallBack() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error calling listChannels: " + e);
            }

            @Override
            public void onResponse(Rpc.ListChannelsResponse response) {
                liveDataResponse.postValue(response);
            }
        });

        return liveDataResponse;
    }

    /**
     * New address.
     */
    public LiveData<Rpc.NewAddressResponse> newAddress() {
        MutableLiveData<Rpc.NewAddressResponse> liveDataResponse = new MutableLiveData<>(null);

        lndClient.newAddress(new LndClient.NewAddressCallBack() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error calling newAddress: " + e);
            }

            @Override
            public void onResponse(Rpc.NewAddressResponse response) {
                liveDataResponse.postValue(response);
            }
        });

        return liveDataResponse;
    }


}
