package io.github.yzernik.squeakand.ui.viewprofile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;

public class ViewProfileModel extends AndroidViewModel {

    private SqueakProfileRepository mRepository;

    public ViewProfileModel(@NonNull Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
    }

    public LiveData<SqueakProfile> getSqueakProfile(int profileId) {
        return mRepository.getSqueakProfile(profileId);
    }

    public void updateProfile(SqueakProfile squeakProfile) {
        mRepository.update(squeakProfile);
    }

}
