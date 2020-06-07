package io.github.yzernik.squeakand.ui.network;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.SqueakServerRepository;

public class NetworkViewModel extends AndroidViewModel {

    private SqueakServerRepository squeakServerRepository;

    public NetworkViewModel(@NonNull Application application) {
        super(application);
        this.squeakServerRepository = SqueakServerRepository.getRepository(application);
    }

    public LiveData<List<SqueakServer>> getSqueakServers() {
        return squeakServerRepository.getLiveServers();
    }

    void insert(SqueakServer squeakServer) {
        squeakServerRepository.insert(squeakServer);
    }

    void delete(SqueakServer squeakServer) {
        squeakServerRepository.delete(squeakServer);
    }

}
