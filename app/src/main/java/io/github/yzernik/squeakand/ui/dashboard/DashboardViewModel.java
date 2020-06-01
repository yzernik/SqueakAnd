package io.github.yzernik.squeakand.ui.dashboard;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;

public class DashboardViewModel extends AndroidViewModel {

    // private MutableLiveData<String> mText;
    private SqueakProfileRepository mRepository;
    private LiveData<List<SqueakProfile>> mAllSqueakProfiles;


    public DashboardViewModel(Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
        mAllSqueakProfiles = mRepository.getAllSqueakProfiles();
    }


    /*    public LiveData<String> getText() {
        return mText;
    }*/

    public LiveData<List<SqueakProfile>> getmAllSqueakProfiles() {
        return mAllSqueakProfiles;
    }

    public void insert(SqueakProfile squeakProfile) {
        mRepository.insert(squeakProfile);
    }


}