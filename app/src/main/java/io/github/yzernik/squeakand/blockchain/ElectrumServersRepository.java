package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ElectrumServersRepository {

    private static volatile ElectrumServersRepository INSTANCE;

    private MutableLiveData<List<ElectrumServerAddress>> liveServers = new MutableLiveData<>();
    private LiveElectrumPeersMap peersMap = new LiveElectrumPeersMap(liveServers);

    private PeerDownloader peerDownloader;

    private ElectrumServersRepository() {
        // Singleton constructor, only called by static method.
        System.out.println("Starting new ElectrumServersRepository");
        peerDownloader = new PeerDownloader(peersMap);
    }

    public static ElectrumServersRepository getRepository() {
        if (INSTANCE == null) {
            synchronized (ElectrumServersRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ElectrumServersRepository();
                }
            }
        }
        return INSTANCE;
    }

    public void initialize() {
        // Start the worker thread.
        peerDownloader.keepPeersUpdated();
    }

    public LiveData<List<ElectrumServerAddress>> getElectrumServers() {
        return liveServers;
    }

    public void addPeer(ElectrumServerAddress serverAddress) {
        peerDownloader.considerAddress(serverAddress);
    }

}
