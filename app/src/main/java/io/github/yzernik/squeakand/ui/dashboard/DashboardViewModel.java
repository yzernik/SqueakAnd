package io.github.yzernik.squeakand.ui.dashboard;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;

public class DashboardViewModel extends AndroidViewModel {

    // private MutableLiveData<String> mText;
    private SqueakProfileRepository mRepository;
    private LiveData<List<SqueakProfile>> mAllSqueakContactProfiles;


    public DashboardViewModel(Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
        mAllSqueakContactProfiles = mRepository.getAllSqueakContactProfiles();
    }


    /*    public LiveData<String> getText() {
        return mText;
    }*/

    public LiveData<List<SqueakProfile>> getmAllSqueakContactProfiles() {
        return mAllSqueakContactProfiles;
    }

    public void insert(SqueakProfile squeakProfile) {
        mRepository.insert(squeakProfile);
    }


}