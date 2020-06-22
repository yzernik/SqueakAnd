package io.github.yzernik.squeakand.ui.viewsqueak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.SqueakServerRepository;
import io.github.yzernik.squeakand.server.SqueakServerAsyncClient;

public class ViewSqueakModel extends AndroidViewModel {

    private Sha256Hash squeakHash;
    private LiveData<SqueakEntryWithProfile> liveSqueak;
    private LiveData<List<SqueakEntryWithProfile>> liveThreadAncestorSqueaks;

    private SqueakRepository mRepository;
    private SqueakServerRepository squeakServerRepository;


    public ViewSqueakModel(Application application, Sha256Hash squeakHash) {
        super(application);
        mRepository = SqueakRepository.getRepository(application);
        squeakServerRepository = SqueakServerRepository.getRepository(application);
        this.squeakHash = squeakHash;
        this.liveSqueak = mRepository.getSqueak(squeakHash);
        this.liveThreadAncestorSqueaks = mRepository.getThreadAncestorSqueaks(squeakHash);
    }

    public LiveData<SqueakEntryWithProfile> getSqueak() {
        return liveSqueak;
    }

    public LiveData<List<SqueakEntryWithProfile>> getThreadAncestorSqueaks() {
        return liveThreadAncestorSqueaks;
    }

    public SqueakServerAsyncClient getAsyncClient() {
        return squeakServerRepository.getSqueakServerAsyncClient();
    }

}