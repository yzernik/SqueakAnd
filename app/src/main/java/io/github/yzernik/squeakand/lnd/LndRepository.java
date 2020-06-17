package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import io.github.yzernik.squeakand.blockchain.ElectrumDownloaderController;

public class LndRepository {

    private static volatile LndRepository INSTANCE;

    private LndClient lndClient;

    // Controller
    ElectrumDownloaderController downloaderConnection;

    private LndRepository(Application application) {
        // Singleton constructor, only called by static method.
        String lndDir = application.getFilesDir() + "/.lnd";
        lndClient = new LndClient(lndDir);

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
        lndClient.start();
    }

    public void getInfo() {
        // TODO
        Log.i(getClass().getName(), "Getting info...");
        lndClient.initWallet();
        lndClient.getInfo();
    }

}
