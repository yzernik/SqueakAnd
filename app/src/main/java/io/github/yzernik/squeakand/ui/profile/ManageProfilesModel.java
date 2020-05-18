package io.github.yzernik.squeakand.ui.profile;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;

public class ManageProfilesModel extends AndroidViewModel {

    private SqueakProfileRepository mRepository;
    private SharedPreferences sharedPreferences;
    private LiveData<List<SqueakProfile>> mAllSqueakProfiles;
    private MutableLiveData<Integer> mSelectedSqueakProfileId;

    public ManageProfilesModel(Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
        mAllSqueakProfiles = mRepository.getAllSqueakProfiles();
    }

    public LiveData<List<SqueakProfile>> getmAllSqueakProfiles() {
        return mAllSqueakProfiles;
    }

    public void insert(SqueakProfile squeakProfile) {
        mRepository.insert(squeakProfile);
    }

}
