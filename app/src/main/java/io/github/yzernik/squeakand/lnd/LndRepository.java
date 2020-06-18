package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.blockchain.ElectrumDownloaderController;
import lnrpc.Rpc;
import lnrpc.Walletunlocker;

public class LndRepository {

    private static volatile LndRepository INSTANCE;

    // private LndClient lndClient;
    private LndController lndController;

    // Controller
    ElectrumDownloaderController downloaderConnection;

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
        lndController.rmLndDir();

        // Start the lnd node
        lndController.start();

        // Initialize the wallet
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lndController.initWallet();
    }

    public LiveData<Walletunlocker.InitWalletResponse> initWallet() {
        // TODO
        Log.i(getClass().getName(), "Initializing wallet...");

        // Generate the wallet seed and initialize the wallet.
        return lndController.initWallet();
    }


    public LiveData<Rpc.GetInfoResponse> getInfo() {
        Log.i(getClass().getName(), "Getting info...");
        return lndController.getInfo();
    }

}
