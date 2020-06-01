package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.MutableLiveData;

public class ElectrumDownloaderConnection {

    private MutableLiveData<ServerUpdate> liveServerUpdate;

    public ElectrumDownloaderConnection(MutableLiveData<ServerUpdate> liveServerUpdate) {
        this.liveServerUpdate = liveServerUpdate;
        setStatusDisconnected(null);
    }

    void setStatusConnected(ElectrumServerAddress serverAddress, BlockInfo blockInfo) {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.CONNECTED,
                serverAddress,
                blockInfo
        );
        liveServerUpdate.postValue(serverUpdate);
    }

    void setStatusDisconnected(ElectrumServerAddress serverAddress) {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.DISCONNECTED,
                serverAddress,
                null
        );
        liveServerUpdate.postValue(serverUpdate);
    }

    void setStatusConnecting(ElectrumServerAddress serverAddress) {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.CONNECTING,
                serverAddress,
                null
        );
        liveServerUpdate.postValue(serverUpdate);
    }

}
