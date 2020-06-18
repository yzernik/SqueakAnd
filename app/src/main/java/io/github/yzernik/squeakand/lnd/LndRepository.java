package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import lnrpc.Rpc;

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
        lndController.start();

        // Wait a few seconds for lnd to start
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Unlock the existing wallet
        lndController.unlockWallet();

        // Wait a few seconds
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Initialize a new wallet
        lndController.initWallet();
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
