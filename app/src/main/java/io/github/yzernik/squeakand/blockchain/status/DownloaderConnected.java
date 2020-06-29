package io.github.yzernik.squeakand.blockchain.status;

import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;
import io.github.yzernik.squeakand.blockchain.ServerUpdate;

public class DownloaderConnected implements ElectrumDownloaderStatus {

    private ElectrumServerAddress serverAddress;
    private BlockInfo blockInfo;

    public DownloaderConnected(ElectrumServerAddress serverAddress, BlockInfo blockInfo) {
        this.serverAddress = serverAddress;
        this.blockInfo = blockInfo;
    }

    @Override
    public ElectrumServerAddress getServerAddress() {
        return serverAddress;
    }

    @Override
    public ServerUpdate.ConnectionStatus getConnectionStatus() {
        return ServerUpdate.ConnectionStatus.CONNECTED;
    }

    @Override
    public BlockInfo getLatestBlockInfo() {
        return blockInfo;
    }
}
