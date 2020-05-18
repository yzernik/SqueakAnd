package io.github.yzernik.squeakand.ui.createtodo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;

public class CreateTodoModel extends AndroidViewModel {

    private static final String SQUEAK_PROFILE_FILE_KEY = "io.github.yzernik.squeakand.SQUEAK_PROFILE_PREFERENCES";
    private static final String SELECTED_SQUEAK_PROFILE_ID_KEY = "SELECTED_SQUEAK_PROFILE_ID";

    private SqueakProfileRepository mRepository;
    private SharedPreferences sharedPreferences;
    private LiveData<List<SqueakProfile>> mAllSqueakProfiles;
    private MutableLiveData<Integer> mSelectedSqueakProfileId;
    public Sha256Hash replyToHash;

    public CreateTodoModel(Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
        mAllSqueakProfiles = mRepository.getAllSqueakProfiles();
        mSelectedSqueakProfileId = new MutableLiveData<>();
        sharedPreferences = application.getSharedPreferences(
                SQUEAK_PROFILE_FILE_KEY, Context.MODE_PRIVATE);
        replyToHash = null;

        // Set the initial value of squeakprofile id
        loadSelectedSqueakProfileId();
    }

    LiveData<List<SqueakProfile>> getmAllSqueakProfiles() {
        return mAllSqueakProfiles;
    }

    void setSelectedSqueakProfileId(int squeakProfileId) {
        mSelectedSqueakProfileId.setValue(squeakProfileId);
        saveSelectedProfileId(squeakProfileId);
    }

    private void loadSelectedSqueakProfileId() {
        int currentSqueakProfileId = getSavedSelectedProfileId();
        mSelectedSqueakProfileId.setValue(currentSqueakProfileId);
    }

    private int getSavedSelectedProfileId() {
        return sharedPreferences.getInt(SELECTED_SQUEAK_PROFILE_ID_KEY, -1);
    }

    private void saveSelectedProfileId(int squeakProfileId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SELECTED_SQUEAK_PROFILE_ID_KEY, squeakProfileId);
        editor.commit();
    }

    LiveData<SqueakProfile> getSelectedSqueakProfile() {
        return Transformations.switchMap(mAllSqueakProfiles, profiles -> {
            return Transformations.map(mSelectedSqueakProfileId, profileId -> {
                for (SqueakProfile profile: profiles) {
                    if (profile.getProfileId() == profileId) {
                        Log.i(getClass().getName(), "Returning profile: " + profile);
                        return profile;
                    }
                }
                return null;
            });
        });
    }

    LiveData<CreateSqueakParams> getCreateSqueakParams() {
        return Transformations.map(getSelectedSqueakProfile(), profile -> {
            return new CreateSqueakParams(profile, replyToHash);
        });
    }

}
