package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class PeersMapLiveData implements ElectrumPeersMap.PeersChangedHandler {

    private MutableLiveData<List<ElectrumServerAddress>> liveServers;

    public PeersMapLiveData() {
        this.liveServers = new MutableLiveData<>();
    }

    public LiveData<List<ElectrumServerAddress>> getLiveServers() {
        return liveServers;
    }

    @Override
    public void handleChange(List<ElectrumServerAddress> peers) {
        liveServers.postValue(peers);
    }

}
