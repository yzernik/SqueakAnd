package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import io.github.yzernik.squeakand.preferences.Preferences;

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

    private CountDownLatch serverStartedLatch;
    private CountDownLatch walletUnlockedLatch;

    private LndControllerUpdateHandler lndControllerUpdateHandler;
    private LndWalletStatus lndWalletStatus;

    public LndController(Application application, String network, LndControllerUpdateHandler lndControllerUpdateHandler, String password) {
        this.lndDir = Paths.get(application.getFilesDir().toString(), LND_DIR_RELATIVE_PATH).toString();
        this.network = network;
        this.password = password;
        this.lndClient = new LndClient();
        this.preferences = new Preferences(application);
        this.lndSyncClient = new LndSyncClient();

        this.lndControllerUpdateHandler = lndControllerUpdateHandler;
        this.lndWalletStatus = new LndWalletStatus();

        this.serverStartedLatch = new CountDownLatch(1);
        this.walletUnlockedLatch = new CountDownLatch(1);

        // Update the lndWalletStatus to know if there is an existing wallet.
        updateWalletExists();
    }

    public LndController(Application application, String network, LndControllerUpdateHandler lndControllerUpdateHandler) {
        this(application, network, lndControllerUpdateHandler, DEFAULT_PASSWORD);
    }

    /**
     * Start the lnd node.
     */
    public void start() {
        this.serverStartedLatch = new CountDownLatch(1);
        this.walletUnlockedLatch = new CountDownLatch(1);
        lndClient.start(lndDir, network, new LndClient.StartCallBack() {
            @Override
            public void onError1(Exception e) {
                Log.e(getClass().getName(), "Failed to start lnd daemon: " + e);
                System.exit(1);
            }

            @Override
            public void onResponse1() {
                serverStartedLatch.countDown();
                setDaemonStarted(true);
            }

            @Override
            public void onError2(Exception e) {
                Log.e(getClass().getName(), "Failed to be ready for RPC in lnd daemon: " + e);
                System.exit(1);
            }

            @Override
            public void onResponse2() {
                walletUnlockedLatch.countDown();
                setRpcReady(true);
            }
        });
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
        Log.i(getClass().getName(),"InitWallet and saved seed words.");
        assert getSeedWords().length == 24;
        updateWalletExists();
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

    public void waitForServerStarted() throws InterruptedException {
        serverStartedLatch.await();
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
        updateWalletExists();
    }

    /**
     * Unlock the existing wallet if there is one already,
     * or create a new wallet, depending on if there are saved
     * seed words.
     *
     */
    public void initialize() {
        start();
        try {
            waitForServerStarted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private void setDaemonStarted(boolean isDaemonRuning) {
        lndWalletStatus.setDaemonRunning(isDaemonRuning);
        lndControllerUpdateHandler.setWalletStatus(lndWalletStatus);
    }

    private void setWalletExists(boolean hasWallet) {
        lndWalletStatus.setWalletExists(hasWallet);
        lndControllerUpdateHandler.setWalletStatus(lndWalletStatus);
    }

    private void setRpcReady(boolean isRpcReady) {
        lndWalletStatus.setRpcReady(isRpcReady);
        lndControllerUpdateHandler.setWalletStatus(lndWalletStatus);
    }

    private void updateWalletExists() {
        setWalletExists(hasWallet());
    }


    public interface LndControllerUpdateHandler {
        void setWalletStatus(LndWalletStatus status);
        void onRpcReady();
    }

}
