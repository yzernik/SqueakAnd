package io.github.yzernik.squeakand.ui.viewcontact;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;

public class ViewContactModel extends AndroidViewModel {

    private SqueakProfileRepository mRepository;

    public ViewContactModel(@NonNull Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
    }

    public LiveData<SqueakProfile> getSqueakContactProfile(int profileId) {
        return mRepository.getSqueakProfile(profileId);
    }

}
