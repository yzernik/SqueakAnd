package io.github.yzernik.squeakand.blockchain;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Block;

import java.util.List;
import java.util.concurrent.Future;

import io.github.yzernik.squeakand.blockchain.status.ElectrumDownloaderStatus;
import io.github.yzernik.squeakand.preferences.Preferences;


public class ElectrumBlockchainRepository {

    private static volatile ElectrumBlockchainRepository INSTANCE;

    private ElectrumPeersMap peersMap;

    // For handling download
    private BlockDownloader blockDownloader;
    private Preferences preferences;

    // For handling peers
    private PeerDownloader peerDownloader;

    // For making getheader requests to the electrum server
    private BlockGetter blockGetter;

    // Controller
    private ElectrumConnection electrumConnection;

    // For getting livedata
    private ServerUpdateLiveData serverUpdateLiveData;
    private PeersMapLiveData peersMapLiveData;

    private ElectrumBlockchainRepository(Application application) {
        // Singleton constructor, only called by static method.

        // Create the live data reporters.
        peersMapLiveData = new PeersMapLiveData();
        serverUpdateLiveData = new ServerUpdateLiveData();

        // Create the controllers.
        peersMap = new ElectrumPeersMap(peersMapLiveData);
        electrumConnection = new ElectrumConnection(serverUpdateLiveData);

        // Run the tasks.
        blockDownloader = new BlockDownloader(electrumConnection);
        preferences = new Preferences(application);
        peerDownloader = new PeerDownloader(peersMap);
        blockGetter = new BlockGetter(electrumConnection);
    }

    public static ElectrumBlockchainRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (ElectrumBlockchainRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ElectrumBlockchainRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public void initialize() {
        Log.i(getClass().getName(), "Initializing electrum connection...");

        // Load the server address from sharedpreferences
        ElectrumServerAddress serverAddress = preferences.getElectrumServerAddress();
        if (serverAddress != null) {
            setServer(serverAddress);
        }

        // Start the peer discovery
        peerDownloader.keepPeersUpdated();
    }

    public ElectrumConnection getElectrumConnection() {
        return electrumConnection;
    }

    public void setServer(ElectrumServerAddress serverAddress) {
        // Set up electrum client with server config, and load livedata.
        Log.i(getClass().getName(), "Setting electrum server: " + serverAddress);
        // TODO: update the server address in the controller.
        electrumConnection.setCurrentDownloadServer(serverAddress);
        blockDownloader.reset();

        // Save the server address in sharedpreferences
        preferences.saveElectrumServerAddress(serverAddress);
    }

    public Future<Block> getBlockHash(int blockHeight) {
        return blockGetter.getBlockHeader(blockHeight);
    }

    public LiveData<ElectrumDownloaderStatus> getServerUpdate() {
        return serverUpdateLiveData.getLiveServerUpdate();
    }

    public LiveData<List<ElectrumServerAddress>> getElectrumServers() {
        return peersMapLiveData.getLiveServers();
    }

}
