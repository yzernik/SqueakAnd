package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class PeersMapLiveData {

    private MutableLiveData<List<ElectrumServerAddress>> liveServers;

    public PeersMapLiveData() {
        this.liveServers = new MutableLiveData<>();
    }

    /**
     * Report every new list of peers to the mutable livedata.
     * @param peersMap
     */
    public void reportPeersMap(ElectrumPeersMap peersMap) {
        peersMap.setPeersChangedHandler(peers -> {
            liveServers.postValue(peers);
        });
    }

    public LiveData<List<ElectrumServerAddress>> getLiveServers() {
        return liveServers;
    }


}
