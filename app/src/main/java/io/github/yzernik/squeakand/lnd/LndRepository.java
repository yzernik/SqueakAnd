package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import lnrpc.Rpc;
import lnrpc.Walletunlocker;

public class LndRepository {

    private static volatile LndRepository INSTANCE;

    // private LndClient lndClient;
    private LndController lndController;

    private LndRepository(Application application) {
        // Singleton constructor, only called by static method.
        lndController = new LndController(application, "testnet");

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

    public void initialize() {
        Log.i(getClass().getName(), "LndRepository: Calling initialize ...");

        // Initialize everything in the background thread.
        new Thread( new Runnable() { @Override public void run() {

            // Start the lnd node
            try {
                String startResult = lndController.start();
                Log.i(getClass().getName(), "Started node with result: " + startResult);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                Log.i(getClass().getName(), "Failed to start lnd node.");
                System.exit(1);
            }

            // Unlock the wallet
            boolean walletUnlocked = false;

            // Unlock the existing wallet
            try {
                Walletunlocker.UnlockWalletResponse unlockResult = lndController.unlockWallet();
                Log.i(getClass().getName(), "Unlocked wallet with result: " + unlockResult);
                walletUnlocked = true;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                Log.i(getClass().getName(), "Failed to unlock wallet.");
                // System.exit(1);
            }

            if (walletUnlocked) {
                return;
            }

            // Create a new wallet
            try {
                String[] seedWords = lndController.genSeed();
                Walletunlocker.InitWalletResponse initWalletResult = lndController.initWallet(seedWords);
                Log.i(getClass().getName(), "Initialized wallet with result: " + initWalletResult);
                walletUnlocked = true;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                Log.i(getClass().getName(), "Failed to initialize wallet.");
            }

            if (!walletUnlocked) {
                System.exit(1);
            }


        }}).start();
    }

    public LiveData<Rpc.GetInfoResponse> getInfo() {
        Log.i(getClass().getName(), "Getting info...");
        MutableLiveData<Rpc.GetInfoResponse> liveGetInfoResponse = new MutableLiveData<>();
        try {
            Future<Rpc.GetInfoResponse> responseFuture = lndController.getInfoAsync();
            Rpc.GetInfoResponse getInfoResponse = responseFuture.get();
            liveGetInfoResponse.postValue(getInfoResponse);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return liveGetInfoResponse;
    }

    public LiveData<Rpc.WalletBalanceResponse> walletBalance() {
        Log.i(getClass().getName(), "Getting walletBalance...");
        return lndController.walletBalance();
    }

    public LiveData<Rpc.ListChannelsResponse> listChannels() {
        Log.i(getClass().getName(), "Getting listChannels...");
        return lndController.listChannels();
    }

    public LiveData<Rpc.NewAddressResponse> newAddress() {
        Log.i(getClass().getName(), "Getting newAddress...");
        return lndController.newAddress();
    }

}
