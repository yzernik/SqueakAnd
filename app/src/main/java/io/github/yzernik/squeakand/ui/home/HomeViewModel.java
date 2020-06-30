package io.github.yzernik.squeakand.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.SqueakControllerRepository;
import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.server.SqueakNetworkAsyncClient;

public class HomeViewModel extends AndroidViewModel {

    private SqueakRepository mSqueakRepository;
    private SqueakControllerRepository squeakControllerRepository;
    private LiveData<List<SqueakEntry>> mAllSqueaks;
    private LiveData<List<SqueakEntryWithProfile>> mAllSqueaksWithProfile;

    public HomeViewModel(Application application) {
        super(application);
        mSqueakRepository = SqueakRepository.getRepository(application);
        squeakControllerRepository = SqueakControllerRepository.getRepository(application);
        mAllSqueaks = mSqueakRepository.getAllSqueaks();
        mAllSqueaksWithProfile = mSqueakRepository.getAllSqueaksWithProfile();
    }

    LiveData<List<SqueakEntry>> getAllSqueaks() {
        return mAllSqueaks;
    }

    LiveData<List<SqueakEntryWithProfile>> getmAllSqueaksWithProfile() {
        return mAllSqueaksWithProfile;
    }

    public SqueakNetworkAsyncClient getSqueakServerAsyncClient() {
        return squeakControllerRepository.getSqueakServerAsyncClient();
    }

    public LiveData<DataResult<Integer>> syncTimeline() {
        return squeakControllerRepository.syncTimeline();
    }

}