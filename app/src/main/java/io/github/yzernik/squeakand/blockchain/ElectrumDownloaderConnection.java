package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.MutableLiveData;

public class ElectrumDownloaderConnection {

    private MutableLiveData<ServerUpdate> liveServerUpdate;
    private LiveElectrumPeersMap peersMap;

    public ElectrumDownloaderConnection(MutableLiveData<ServerUpdate> liveServerUpdate, LiveElectrumPeersMap peersMap) {
        this.liveServerUpdate = liveServerUpdate;
        this.peersMap = peersMap;
        setStatusDisconnected(null);
    }

    void setStatusConnected(ElectrumServerAddress serverAddress, BlockInfo blockInfo) {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.CONNECTED,
                serverAddress,
                blockInfo
        );
        liveServerUpdate.postValue(serverUpdate);

        // Add the connected address to the peers map
        peersMap.putNewPeer(serverAddress);
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
