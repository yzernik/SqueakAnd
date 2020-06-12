package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ElectrumDownloaderConnection {

    private MutableLiveData<List<ElectrumServerAddress>> liveServers;
    private MutableLiveData<ServerUpdate> liveServerUpdate;

    private ElectrumServerAddress currentDownloadServer;
    private LiveElectrumPeersMap peersMap;


    public ElectrumDownloaderConnection() {
        this.liveServerUpdate = new MutableLiveData<>();
        this.liveServers = new MutableLiveData<>();
        this.peersMap = new LiveElectrumPeersMap(liveServers);

        this.currentDownloadServer = null;
        setStatusDisconnected();
    }

    void setStatusConnected(BlockInfo blockInfo) {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.CONNECTED,
                currentDownloadServer,
                blockInfo
        );
        liveServerUpdate.postValue(serverUpdate);

        // Add the connected address to the peers map
        peersMap.putNewPeer(currentDownloadServer);
    }

    void setStatusDisconnected() {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.DISCONNECTED,
                currentDownloadServer,
                null
        );
        liveServerUpdate.postValue(serverUpdate);
    }

    void setStatusConnecting() {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.CONNECTING,
                currentDownloadServer,
                null
        );
        liveServerUpdate.postValue(serverUpdate);
    }

    public MutableLiveData<ServerUpdate> getLiveServerUpdate() {
        return liveServerUpdate;
    }

    public LiveElectrumPeersMap getPeersMap() {
        return peersMap;
    }

    public LiveData<List<ElectrumServerAddress>> getLiveServers() {
        return liveServers;
    }

    public synchronized void setCurrentDownloadServer(ElectrumServerAddress serverAddress) {
        currentDownloadServer = serverAddress;
    }

    public ElectrumServerAddress getCurrentDownloadServer() {
        return currentDownloadServer;
    }

}
