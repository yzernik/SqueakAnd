package io.github.yzernik.squeakand.blockchain;

/**
 * For controlling which electrum server to download from, and to
 * maintain the list of available electrum servers in the network.
 */
public class ElectrumDownloaderController {

    private ElectrumServerAddress currentDownloadServer;
    private ServerUpdateHandler serverUpdateHandler;
    private ServerUpdate latestUpdate;

    public ElectrumDownloaderController() {
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
