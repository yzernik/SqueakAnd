package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

/**
 * For controlling which electrum server to download from, and to
 * maintain the list of available electrum servers in the network.
 */
public class ElectrumDownloaderController {

    private MutableLiveData<List<ElectrumServerAddress>> liveServers;

    private ElectrumServerAddress currentDownloadServer;
    private LiveElectrumPeersMap peersMap;

    private ServerUpdateHandler serverUpdateHandler;

    private ServerUpdate latestUpdate;

    public ElectrumDownloaderController() {
        this.liveServers = new MutableLiveData<>();
        this.peersMap = new LiveElectrumPeersMap(liveServers);

        this.serverUpdateHandler = null;

        this.latestUpdate = null;
        this.currentDownloadServer = null;
        setStatusDisconnected();
    }

    void setStatusConnected(BlockInfo blockInfo) {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.CONNECTED,
                currentDownloadServer,
                blockInfo
        );
        setServerUpdate(serverUpdate);

        // Add the connected address to the peers map
        peersMap.putNewPeer(currentDownloadServer);
    }

    void setStatusDisconnected() {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.DISCONNECTED,
                currentDownloadServer,
                null
        );
        setServerUpdate(serverUpdate);
    }

    void setStatusConnecting() {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.CONNECTING,
                currentDownloadServer,
                null
        );
        setServerUpdate(serverUpdate);
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

    public synchronized void setServerUpdate(ServerUpdate serverUpdate) {
        this.latestUpdate = serverUpdate;
        handleUpdate(serverUpdate);
    }

    public ElectrumServerAddress getCurrentDownloadServer() {
        return currentDownloadServer;
    }

    public ServerUpdate getCurrentStatusUpdate() {
        return latestUpdate;
    }

    private void handleUpdate(ServerUpdate serverUpdate) {
        if (serverUpdateHandler != null) {
            serverUpdateHandler.handleUpdate(serverUpdate);
        }
    }

    public void setServerUpdateHandler(ServerUpdateHandler serverUpdateHandler) {
        this.serverUpdateHandler = serverUpdateHandler;
    }

    public interface ServerUpdateHandler {
        void handleUpdate(ServerUpdate serverUpdate);
    }

}
