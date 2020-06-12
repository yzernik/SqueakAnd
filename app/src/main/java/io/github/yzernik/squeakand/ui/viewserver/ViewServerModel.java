package io.github.yzernik.squeakand.ui.viewserver;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.SqueakServerRepository;

public class ViewServerModel extends AndroidViewModel {

    private SqueakServerRepository mRepository;

    public ViewServerModel(@NonNull Application application) {
        super(application);
        mRepository = new SqueakServerRepository(application);
    }

    public LiveData<SqueakServer> getSqueakServer(int serverId) {
        return mRepository.getSqueakServer(serverId);
    }

    public void updateServer(SqueakServer squeakServer) {
        mRepository.update(squeakServer);
    }

    public void deleteServer(SqueakServer squeakServer) {
        mRepository.delete(squeakServer);
    }

}
