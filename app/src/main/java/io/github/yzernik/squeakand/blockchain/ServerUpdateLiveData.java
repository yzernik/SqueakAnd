package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ServerUpdateLiveData implements ElectrumDownloaderController.ServerUpdateHandler {

    private MutableLiveData<ServerUpdate> liveServerUpdate;

    public ServerUpdateLiveData() {
        this.liveServerUpdate = new MutableLiveData<>();
    }

    public LiveData<ServerUpdate> getLiveServerUpdate() {
        return liveServerUpdate;
    }

    @Override
    public void handleUpdate(ServerUpdate serverUpdate) {
        liveServerUpdate.postValue(serverUpdate);
    }

}
