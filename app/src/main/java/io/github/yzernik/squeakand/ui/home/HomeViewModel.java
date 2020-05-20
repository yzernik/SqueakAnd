package io.github.yzernik.squeakand.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakRepository;

public class HomeViewModel extends AndroidViewModel {

    private SqueakRepository mSqueakRepository;
    private LiveData<List<SqueakEntry>> mAllSqueaks;
    private LiveData<List<SqueakEntryWithProfile>> mAllSqueaksWithProfile;

    public HomeViewModel(Application application) {
        super(application);
        mSqueakRepository = new SqueakRepository(application);
        mAllSqueaks = mSqueakRepository.getAllSqueaks();
        mAllSqueaksWithProfile = mSqueakRepository.getAllSqueaksWithProfile();
    }

    LiveData<List<SqueakEntry>> getAllSqueaks() {
        return mAllSqueaks;
    }

    LiveData<List<SqueakEntryWithProfile>> getmAllSqueaksWithProfile() {
        return mAllSqueaksWithProfile;
    }
}