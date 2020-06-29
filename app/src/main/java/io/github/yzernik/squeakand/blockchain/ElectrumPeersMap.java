package io.github.yzernik.squeakand.blockchain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ElectrumPeersMap extends ConcurrentHashMap<ElectrumServerAddress, Long> {

    private PeersChangedHandler peersChangedHandler;

    public ElectrumPeersMap(PeersChangedHandler peersChangedHandler) {
        this.peersChangedHandler = peersChangedHandler;
        handlePeersChanged();
    }

    @Nullable
    @Override
    public Long remove(@NonNull Object key) {
        Long ret = super.remove(key);
        handlePeersChanged();
        return ret;
    }

    @Nullable
    @Override
    public Long put(@NonNull ElectrumServerAddress key, @NonNull Long value) {
        Long ret = super.put(key, value);
        handlePeersChanged();
        return ret;
    }

    @Nullable
    public Long putNewPeer(@NonNull ElectrumServerAddress key) {
        return put(key, getCurrentTimeMs());
    }

    private long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }

    public void handlePeersChanged() {
        ArrayList<ElectrumServerAddress> keyList = new ArrayList<ElectrumServerAddress>(this.keySet());
        peersChangedHandler.handleChange(keyList);
    }

    public interface PeersChangedHandler {
        void handleChange(List<ElectrumServerAddress> peers);
    }

}
