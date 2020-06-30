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

    private static final String LND_DIR_RELATIVE_PATH = "/.lnd";
    // TODO: Use a real password.
    private static final String DEFAULT_PASSWORD = "somesuperstrongpw";

    private final String lndDir;
    private final String network;
    private final String password;
    private final LndClient lndClient;
    private final Preferences preferences;
    private final LndSyncClient lndSyncClient;


    public LndController(Application application, String network, String password) {
        this.lndDir = Paths.get(application.getFilesDir().toString(), LND_DIR_RELATIVE_PATH).toString();
        this.network = network;
        this.password = password;
        this.lndClient = new LndClient();
        this.preferences = new Preferences(application);
        this.lndSyncClient = new LndSyncClient(application);
    }

    public LndController(Application application, String network) {
        this(application, network, DEFAULT_PASSWORD);
    }

    /**
     * Start the lnd node.
     */
    public String start() throws InterruptedException, ExecutionException, TimeoutException {
        return lndSyncClient.start(lndDir, network);
    }

    /**
     * Stop the lnd node.
     */
    public Rpc.StopResponse stop() throws InterruptedException, ExecutionException, TimeoutException {
        return lndSyncClient.stop();
    }

    /**
     * Unlock the wallet.
     */
    public Walletunlocker.UnlockWalletResponse unlockWallet() throws InterruptedException, ExecutionException, TimeoutException {
        return lndSyncClient.unlockWallet(password);
    }

    /**
     * Generate new seed words.
     */
    public String[] genSeed() throws InterruptedException, ExecutionException, TimeoutException {
        return lndSyncClient.genSeed();
    }

    /**
     * Initialize new wallet with given seed words
     * @param seedWords
     */
    public Walletunlocker.InitWalletResponse initWallet(String[] seedWords) throws InterruptedException, ExecutionException, TimeoutException {
        return lndSyncClient.initWallet(seedWords, password);
    }

    /**
     * Delete the lnd data directory.
     */
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
}
