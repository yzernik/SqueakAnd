package io.github.yzernik.squeakand.blockchain;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.github.yzernik.squeakand.blockchain.status.DownloaderConnected;
import io.github.yzernik.squeakand.blockchain.status.DownloaderConnecting;
import io.github.yzernik.squeakand.blockchain.status.DownloaderDisconnected;
import io.github.yzernik.squeakand.blockchain.status.ElectrumDownloaderStatus;

/**
 * For controlling which electrum server to download from, and to
 * maintain the list of available electrum servers in the network.
 */
public class ElectrumConnection {

    private ElectrumServerAddress currentDownloadServer;
    private ElectrumDownloaderStatus status;
    private ServerUpdateHandler serverUpdateHandler;

    private ReadWriteLock serverLock = new ReentrantReadWriteLock();
    private ReadWriteLock statusLock = new ReentrantReadWriteLock();

    public ElectrumConnection(ServerUpdateHandler serverUpdateHandler) {
        this.serverUpdateHandler = serverUpdateHandler;

        this.status = null;
        this.currentDownloadServer = null;
        setStatusDisconnected();
    }

    void setStatusConnected(BlockInfo blockInfo) {
        setServerUpdate(new DownloaderConnected(currentDownloadServer, blockInfo));
    }

    void setStatusDisconnected() {
        setServerUpdate(new DownloaderDisconnected());
    }

    void setStatusConnecting() {
        setServerUpdate(new DownloaderConnecting(currentDownloadServer));
    }

    public void setCurrentDownloadServer(ElectrumServerAddress serverAddress) {
        serverLock.writeLock().lock();
        try {
            currentDownloadServer = serverAddress;
        } finally {
            serverLock.writeLock().unlock();
        }
    }

    public void setServerUpdate(ElectrumDownloaderStatus status) {
        statusLock.writeLock().lock();
        try {
            this.status = status;
        } finally {
            statusLock.writeLock().unlock();
        }
        handleUpdate(status);
    }

    public ElectrumServerAddress getCurrentDownloadServer() {
        serverLock.readLock().lock();
        try {
            return currentDownloadServer;
        } finally {
            serverLock.readLock().unlock();
        }
    }

    public ElectrumDownloaderStatus getCurrentStatusUpdate() {
        statusLock.readLock().lock();
        try {
            return status;
        } finally {
            statusLock.readLock().unlock();
        }
    }

    private void handleUpdate(ElectrumDownloaderStatus status) {
        serverUpdateHandler.handleUpdate(status);
    }

    public interface ServerUpdateHandler {
        void handleUpdate(ElectrumDownloaderStatus status);
    }

}
