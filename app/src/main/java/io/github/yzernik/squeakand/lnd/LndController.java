package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.yzernik.squeakand.preferences.Preferences;
import lnrpc.Rpc;

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

    private CountDownLatch walletUnlockedLatch;

    public LndController(Application application, String network, String password) {
        this.lndDir = Paths.get(application.getFilesDir().toString(), LND_DIR_RELATIVE_PATH).toString();
        this.network = network;
        this.password = password;
        this.lndClient = new LndClient();
        this.preferences = new Preferences(application);
        this.lndSyncClient = new LndSyncClient();

        this.walletUnlockedLatch = new CountDownLatch(1);
    }

    public LndController(Application application, String network) {
        this(application, network, DEFAULT_PASSWORD);
    }

    /**
     * Start the lnd node.
     */
    public void start() {
        try {
            lndSyncClient.start(lndDir, network);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the lnd node.
     */
    public void stop() {
        try {
            lndSyncClient.stop();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unlock the wallet.
     */
    public void unlockWallet() {
        try {
            lndSyncClient.unlockWallet(password);
            // Pause for one second.
            Thread.sleep(5000);
            setWalletUnlocked();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
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
    public void initWallet(String[] seedWords) throws InterruptedException, ExecutionException, TimeoutException {
        lndSyncClient.initWallet(seedWords, password);
        // Save the seed words immediately after the init wallet response.
        saveSeedWords(seedWords);
        Thread.sleep(5000);
        // unlockWallet();
        Thread.sleep(5000);
        setWalletUnlocked();
        Log.i(getClass().getName(),"InitWallet and saved seed words.");
        assert getSeedWords().length == 24;
    }

    public void initWallet() throws InterruptedException {
        try {
            String[] seedWords = genSeed();
            initWallet(seedWords);
        } catch (ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void deleteWallet() {
        stop();
        rmLndDir();
        clearSeedWords();
        start();
    }

    /**
     * Return true is the wallet is unlocked.
     * @return boolean
     */
    public boolean isWalletUnlocked() {
        return walletUnlockedLatch.getCount() == 0;
    }

    public void waitForWalletUnlocked() throws InterruptedException {
        walletUnlockedLatch.await();
    }

    /**
     * Return true if the wallet exists.
     * @return
     */
    public boolean hasWallet() {
        return hasSavedSeedWords();
    }

    private boolean hasSavedSeedWords() {
        String[] seedWords = getSeedWords();
        return seedWords != null && seedWords.length != 0;
    }

    private void saveSeedWords(String[] seedWords) {
        preferences.saveWalletSeed(seedWords);
    }

    public String[] getSeedWords() {
        return preferences.getWalletSeed();
    }

    private void clearSeedWords() {
        preferences.clearWalletSeed();
    }

    /**
     * Unlock the existing wallet if there is one already,
     * or create a new wallet, depending on if there are saved
     * seed words.
     *
     */
    public void initialize() {
        start();
        Log.i(getClass().getName(), "Started node with result.");
        if (hasWallet()) {
            unlockWallet();
            Log.i(getClass().getName(), "Unlocked wallet.");
        }
    }

    /**
     * Delete the lnd data directory.
     */
    private void rmLndDir() {
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

    private void setWalletUnlocked() {
        walletUnlockedLatch.countDown();
    }

    /*    private void setWalletUnlocked(boolean walletUnlocked) {
        this.walletUnlocked.set(walletUnlocked);
    }

    private void setWalletUnlocked() {
        setWalletUnlocked(true);
    }

    private void setWalletLocked() {
        setWalletUnlocked(false);
    }*/

}
