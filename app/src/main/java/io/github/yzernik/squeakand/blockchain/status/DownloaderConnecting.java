package io.github.yzernik.squeakand.blockchain.status;

import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;
import io.github.yzernik.squeakand.blockchain.ServerUpdate;

public class DownloaderConnecting implements ElectrumDownloaderStatus {

    private ElectrumServerAddress serverAddress;

    public DownloaderConnecting(ElectrumServerAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public ElectrumServerAddress getServerAddress() {
        return serverAddress;
    }

    @Override
    public ServerUpdate.ConnectionStatus getConnectionStatus() {
        return ServerUpdate.ConnectionStatus.CONNECTING;
    }

    @Override
    public BlockInfo getLatestBlockInfo() {
        return null;
    }

}
