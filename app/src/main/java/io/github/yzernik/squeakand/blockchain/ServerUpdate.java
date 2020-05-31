package io.github.yzernik.squeakand.blockchain;

public class ServerUpdate {
    private final ConnectionStatus connectionStatus;
    private final ElectrumServerAddress electrumServerAddress;
    private final BlockInfo blockInfo;

    public ServerUpdate(ConnectionStatus connectionStatus, ElectrumServerAddress electrumServerAddress, BlockInfo blockInfo) {
        this.connectionStatus = connectionStatus;
        this.electrumServerAddress = electrumServerAddress;
        this.blockInfo = blockInfo;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public ElectrumServerAddress getElectrumServerAddress() {
        return electrumServerAddress;
    }

    public BlockInfo getBlockInfo() {
        return blockInfo;
    }

    public enum ConnectionStatus {
        CONNECTED, CONNECTING, DISCONNECTED;
    }
}
