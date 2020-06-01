package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ElectrumServersRepository {

    private static volatile ElectrumServersRepository INSTANCE;

    private MutableLiveData<List<ElectrumServerAddress>> liveServers = new MutableLiveData<>();

    private ConcurrentHashMap<InetSocketAddress, Long> serversMap;

    private PeerDownloader peerDownloader;

    private ElectrumServersRepository() {
        // Singleton constructor, only called by static method.
        System.out.println("Starting new ElectrumServersRepository");
        serversMap = new ConcurrentHashMap<>();
        peerDownloader = new PeerDownloader(liveServers, serversMap);
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
        // Initialize the livedata with an empty list.
        liveServers.setValue(Collections.emptyList());

        // Start the worker thread.
        peerDownloader.keepPeersUpdated();
    }

    public LiveData<List<ElectrumServerAddress>> getElectrumServers() {
        return liveServers;
    }

    public void addPeer() {
        // TODO: call the addpeer method on peerdownloader so that livedata update correctly.
    }

}
