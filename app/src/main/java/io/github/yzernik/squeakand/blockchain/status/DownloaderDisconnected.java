package io.github.yzernik.squeakand.blockchain.status;

import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;
import io.github.yzernik.squeakand.blockchain.ServerUpdate;

public class DownloaderDisconnected implements ElectrumDownloaderStatus {

    @Override
    public ElectrumServerAddress getServerAddress() {
        return null;
    }

    @Override
    public ServerUpdate.ConnectionStatus getConnectionStatus() {
        return ServerUpdate.ConnectionStatus.DISCONNECTED;
    }

    @Override
    public BlockInfo getLatestBlockInfo() {
        return null;
    }

}
