package io.github.yzernik.squeakand.ui.profile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;

public class SelectProfileModel extends AndroidViewModel {

    private static final String SQUEAK_PROFILE_FILE_KEY = "io.github.yzernik.squeakand.SQUEAK_PROFILE_PREFERENCES";
    private static final String SELECTED_SQUEAK_PROFILE_ID_KEY = "SELECTED_SQUEAK_PROFILE_ID";

    private SqueakProfileRepository mRepository;
    private SharedPreferences sharedPreferences;
    private LiveData<List<SqueakProfile>> mAllSqueakProfiles;
    private MutableLiveData<Integer> mSelectedSqueakProfileId;

    public SelectProfileModel(Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
        mAllSqueakProfiles = mRepository.getAllSqueakProfiles();
        mSelectedSqueakProfileId = new MutableLiveData<>();
        sharedPreferences = application.getSharedPreferences(
                SQUEAK_PROFILE_FILE_KEY, Context.MODE_PRIVATE);

        // TODO: set the squeakprofile id
        loadSelectedSqueakProfileId();
    }

    LiveData<List<SqueakProfile>> getmAllSqueakProfiles() {
        return mAllSqueakProfiles;
    }

    void setSelectedSqueakProfileId(int squeakProfileId) {
        this.mSelectedSqueakProfileId.setValue(squeakProfileId);
    }

    LiveData<Integer> getSelectedSqueakProfileId() {
        return mSelectedSqueakProfileId;
    }

    void saveSelectedSqueakProfileId(int squeakProfileId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SELECTED_SQUEAK_PROFILE_ID_KEY, squeakProfileId);
        editor.commit();
    }

    private void loadSelectedSqueakProfileId() {
        int currentSqueakProfileId = sharedPreferences.getInt(SELECTED_SQUEAK_PROFILE_ID_KEY, -1);
        mSelectedSqueakProfileId.setValue(currentSqueakProfileId);
    }

    public LiveData<SqueakProfile> getSelectedSqueakProfile() {
        return Transformations.switchMap(mAllSqueakProfiles, profiles -> {
            LiveData<SqueakProfile> ret = Transformations.map(mSelectedSqueakProfileId, profileId -> {
                for (SqueakProfile profile: profiles) {
                    if (profile.getProfileId() == profileId) {
                        return profile;
                    }
                }
                return null;
            });
            return ret;
        });
    }

}