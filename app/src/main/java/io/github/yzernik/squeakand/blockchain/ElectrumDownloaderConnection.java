package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.MutableLiveData;

public class ElectrumDownloaderConnection {

    private ElectrumServerAddress currentDownloadServer;
    private MutableLiveData<ServerUpdate> liveServerUpdate;
    private LiveElectrumPeersMap peersMap;

    public ElectrumDownloaderConnection(LiveElectrumPeersMap peersMap) {
        this.currentDownloadServer = null;
        this.liveServerUpdate = new MutableLiveData<>();
        this.peersMap = peersMap;
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

    public synchronized void setCurrentDownloadServer(ElectrumServerAddress serverAddress) {
        currentDownloadServer = serverAddress;
    }

    public ElectrumServerAddress getCurrentDownloadServer() {
        return currentDownloadServer;
    }

}
