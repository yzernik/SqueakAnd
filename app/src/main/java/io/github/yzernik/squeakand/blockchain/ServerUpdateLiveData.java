package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ServerUpdateLiveData {

    private MutableLiveData<ServerUpdate> liveServerUpdate;

    public ServerUpdateLiveData() {
        this.liveServerUpdate = new MutableLiveData<>();
    }

    /**
     * Report every new ServerUpdate to the mutable livedata.
     * @param electrumDownloaderController
     */
    public void reportController(ElectrumDownloaderController electrumDownloaderController) {
        electrumDownloaderController.setServerUpdateHandler(serverUpdate -> {
            liveServerUpdate.postValue(serverUpdate);
        });
    }

    public LiveData<ServerUpdate> getLiveServerUpdate() {
        return liveServerUpdate;
    }

}
