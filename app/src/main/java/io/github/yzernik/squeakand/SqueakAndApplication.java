package io.github.yzernik.squeakand;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.lnd.LndRepository;

public class SqueakAndApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!

        // Initialize the network connections.
        initializeNetworkConnections();
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * Initializes the connection to electrum server, etc.
     */
    private void initializeNetworkConnections() {
        // Initialize the electrum repository
        ElectrumBlockchainRepository electrumBlockchainRepository = ElectrumBlockchainRepository.getRepository(this);
        Log.i(getClass().getName(), "Calling electrumBlockchainRepository.initialize...");
        electrumBlockchainRepository.initialize();

        // Initialize the squeakserver repository
        SqueakServerRepository squeakServerRepository = SqueakServerRepository.getRepository(this);
        Log.i(getClass().getName(), "Calling squeakServerRepository.initialize...");
        squeakServerRepository.initialize();

        // Initialize the squeaks repository
        SqueakRepository squeakRepository = SqueakRepository.getRepository(this);
        squeakRepository.initialize();

        // Initialize the lnd node
        LndRepository lndRepository = LndRepository.getRepository(this);
        lndRepository.initialize();
    }
}