package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

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

        // TODO: don't delete lnd dir on startup
        // lndController.rmLndDir();

        // Start the lnd node
        try {
            String startResult = lndController.start();
            Log.i(getClass().getName(), "Started node with result: " + startResult);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            Log.i(getClass().getName(), "Failed to start lnd node.");
        }

        // Unlock the existing wallet
        try {
            Walletunlocker.UnlockWalletResponse unlockResult = lndController.unlockWallet();
            Log.i(getClass().getName(), "Unlocked wallet with result: " + unlockResult);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            Log.i(getClass().getName(), "Failed to unlock wallet.");
            System.exit(1);
        }

        /*
        // Initialize a new wallet
        lndController.initWallet();*/
    }

    public LiveData<Rpc.GetInfoResponse> getInfo() {
        Log.i(getClass().getName(), "Getting info...");
        return lndController.getInfo();
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
