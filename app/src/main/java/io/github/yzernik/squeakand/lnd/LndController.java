package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.github.yzernik.squeakand.preferences.Preferences;
import lnrpc.Walletunlocker;

public class LndController {

    // TODO: Use a real password.
    private static final String DEFAULT_PASSWORD = "somesuperstrongpw";

    private final String lndDir;
    private final String network;
    private final String password;
    private final LndClient lndClient;
    private final Preferences preferences;

    public LndController(Application application, String network, String password) {
        this.lndDir = application.getFilesDir() + "/.lnd";;
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
    public void start() {
        lndClient.start(lndDir, network);
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
                        Log.i(getClass().getName(), "Got initWallet response: " + response);
                    }
                });

            }
        });

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


}