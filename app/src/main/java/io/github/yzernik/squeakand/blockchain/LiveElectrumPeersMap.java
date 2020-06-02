package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LiveElectrumPeersMap extends ConcurrentHashMap<ElectrumServerAddress, Long> {

    MutableLiveData<List<ElectrumServerAddress>> liveServers;

    public LiveElectrumPeersMap(MutableLiveData<List<ElectrumServerAddress>> liveServers) {
        this.liveServers = liveServers;
        updateLiveData();
    }

    @Nullable
    @Override
    public Long remove(@NonNull Object key) {
        Long ret = super.remove(key);
        updateLiveData();
        return ret;
    }

    @Nullable
    @Override
    public Long put(@NonNull ElectrumServerAddress key, @NonNull Long value) {
        Long ret = super.put(key, value);
        updateLiveData();
        return ret;
    }

    @Nullable
    public Long putNewPeer(@NonNull ElectrumServerAddress key) {
        return put(key, getCurrentTimeMs());
    }

    private long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }

    public void updateLiveData() {
        ArrayList<ElectrumServerAddress> keyList = new ArrayList<ElectrumServerAddress>(this.keySet());
        liveServers.postValue(keyList);
    }

}
