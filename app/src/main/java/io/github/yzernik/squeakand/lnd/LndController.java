package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
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
        this.lndSyncClient = new LndSyncClient();
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
        Walletunlocker.InitWalletResponse response = lndSyncClient.initWallet(seedWords, password);
        // Save the seed words immediately after the init wallet response.
        preferences.saveWalletSeed(seedWords);
        return response;
    }

    public boolean hasSavedSeedWords() {
        return preferences.getWalletSeed() != null;
    }

    /**
     * Unlock the existing wallet if there is one already,
     * or create a new wallet, depending on if there are saved
     * seed words.
     *
     */
    public void initialize() {
        try {
            String startResult = start();
            Log.i(getClass().getName(), "Started node with result: " + startResult);

            if (hasSavedSeedWords()) {
                Walletunlocker.UnlockWalletResponse unlockResult = unlockWallet();
                Log.i(getClass().getName(), "Unlocked wallet with result: " + unlockResult);
            } else {
                String[] seedWords = genSeed();
                Walletunlocker.InitWalletResponse initWalletResult = initWallet(seedWords);
                Log.i(getClass().getName(), "Initialized wallet with result: " + initWalletResult);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            System.exit(1);
        }

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
