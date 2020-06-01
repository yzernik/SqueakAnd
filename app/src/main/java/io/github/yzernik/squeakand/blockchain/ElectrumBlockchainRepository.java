package io.github.yzernik.squeakand.blockchain;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.preferences.Preferences;


public class ElectrumBlockchainRepository {

    private static volatile ElectrumBlockchainRepository INSTANCE;

    // For handling download
    private MutableLiveData<ServerUpdate> liveServerUpdate = new MutableLiveData<>();
    private BlockDownloader blockDownloader;
    private Preferences preferences;

    // For handling peers
    private MutableLiveData<List<ElectrumServerAddress>> liveServers = new MutableLiveData<>();
    private LiveElectrumPeersMap peersMap = new LiveElectrumPeersMap(liveServers);
    private PeerDownloader peerDownloader;

    private ElectrumBlockchainRepository(Application application) {
        // Singleton constructor, only called by static method.
        ElectrumDownloaderConnection downloaderConnection = new ElectrumDownloaderConnection(liveServerUpdate, peersMap);
        blockDownloader = new BlockDownloader(downloaderConnection);
        preferences = new Preferences(application);
        peerDownloader = new PeerDownloader(peersMap);

        // Initialize the electrum server connection
        initialize();
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
        blockDownloader.setElectrumServer(serverAddress);

        // Save the server address in sharedpreferences
        preferences.saveElectrumServerAddress(serverAddress);
    }

    public LiveData<Sha256Hash> getBlockHash(int blockHeight) {
        // TODO
        return null;
    }

    public LiveData<ServerUpdate> getServerUpdate() {
        return liveServerUpdate;
    }

    public LiveData<List<ElectrumServerAddress>> getElectrumServers() {
        return liveServers;
    }

}
