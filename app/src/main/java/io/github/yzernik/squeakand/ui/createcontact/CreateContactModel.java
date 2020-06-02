package io.github.yzernik.squeakand.ui.createcontact;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;

public class CreateContactModel extends AndroidViewModel {

    private SqueakProfileRepository mRepository;
    private LiveData<List<SqueakProfile>> mAllSqueakProfiles;

    public CreateContactModel(@NonNull Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
        mAllSqueakProfiles = mRepository.getAllSqueakProfiles();
    }

    void insert(SqueakProfile squeakProfile) {
        mRepository.insert(squeakProfile);
    }

}
