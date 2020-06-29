package io.github.yzernik.squeakand.blockchain;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.Block;

import java.util.List;
import java.util.concurrent.Future;

import io.github.yzernik.squeakand.preferences.Preferences;


public class ElectrumBlockchainRepository {

    private static volatile ElectrumBlockchainRepository INSTANCE;

    private MutableLiveData<List<ElectrumServerAddress>> liveServers;
    private LiveElectrumPeersMap peersMap;

    // For handling download
    private BlockDownloader blockDownloader;
    private Preferences preferences;

    // For handling peers
    private PeerDownloader peerDownloader;

    // For making getheader requests to the electrum server
    private BlockGetter blockGetter;

    // Controller
    ElectrumDownloaderController downloaderConnection;

    // For getting livedata from blockdownloader
    private ServerUpdateLiveData serverUpdateLiveData;

    private ElectrumBlockchainRepository(Application application) {
        // Singleton constructor, only called by static method.
        this.liveServers = new MutableLiveData<>();
        this.peersMap = new LiveElectrumPeersMap(liveServers);

        downloaderConnection = new ElectrumDownloaderController();
        blockDownloader = new BlockDownloader(downloaderConnection);
        preferences = new Preferences(application);
        peerDownloader = new PeerDownloader(peersMap);
        blockGetter = new BlockGetter(downloaderConnection);
        serverUpdateLiveData = new ServerUpdateLiveData();
        serverUpdateLiveData.reportController(downloaderConnection);
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

    public void setServer(ElectrumServerAddress serverAddress) {
        // Set up electrum client with server config, and load livedata.
        Log.i(getClass().getName(), "Setting electrum server: " + serverAddress);
        // TODO: update the server address in the controller.
        downloaderConnection.setCurrentDownloadServer(serverAddress);
        blockDownloader.reset();

        // Save the server address in sharedpreferences
        preferences.saveElectrumServerAddress(serverAddress);
    }

    public Future<Block> getBlockHash(int blockHeight) {
        return blockGetter.getBlockHeader(blockHeight);
    }

    public LiveData<ServerUpdate> getServerUpdate() {
        return serverUpdateLiveData.getLiveServerUpdate();
    }

    public LiveData<List<ElectrumServerAddress>> getElectrumServers() {
        return liveServers;
    }

}
