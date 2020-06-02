package io.github.yzernik.squeakand.ui.manageprofiles;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;

public class ManageProfilesModel extends AndroidViewModel {

    private SqueakProfileRepository mRepository;
    private LiveData<List<SqueakProfile>> mAllSqueakSigningProfiles;

    public ManageProfilesModel(Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
        mAllSqueakSigningProfiles = mRepository.getAllSqueakSigningProfiles();
    }

    public LiveData<List<SqueakProfile>> getmAllSqueakSigningProfiles() {
        return mAllSqueakSigningProfiles;
    }

    public void insert(SqueakProfile squeakProfile) {
        mRepository.insert(squeakProfile);
    }

}
