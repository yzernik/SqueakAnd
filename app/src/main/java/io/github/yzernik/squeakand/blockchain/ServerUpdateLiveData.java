package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.github.yzernik.squeakand.blockchain.status.ElectrumDownloaderStatus;

public class ServerUpdateLiveData implements ElectrumConnection.ServerUpdateHandler {

    private MutableLiveData<ElectrumDownloaderStatus> liveServerUpdate;

    public ServerUpdateLiveData() {
        this.liveServerUpdate = new MutableLiveData<>();
    }

    public LiveData<ElectrumDownloaderStatus> getLiveServerUpdate() {
        return liveServerUpdate;
    }

    @Override
    public void handleUpdate(ElectrumDownloaderStatus status) {
        liveServerUpdate.postValue(status);
    }

}
