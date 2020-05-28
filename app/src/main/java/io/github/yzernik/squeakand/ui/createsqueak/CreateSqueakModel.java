package io.github.yzernik.squeakand.ui.createsqueak;

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

import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;
import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.BlockchainRepository;
import io.github.yzernik.squeakand.blockchain.DummyBlockchainRepository;
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;

public class CreateSqueakModel extends AndroidViewModel {

    private static final String SQUEAK_PROFILE_FILE_KEY = "io.github.yzernik.squeakand.SQUEAK_PROFILE_PREFERENCES";
    private static final String SELECTED_SQUEAK_PROFILE_ID_KEY = "SELECTED_SQUEAK_PROFILE_ID";

    private SqueakProfileRepository mProfileRepository;
    private SqueakRepository mSqueakRepository;
    private ElectrumBlockchainRepository blockchainRepository;
    private SharedPreferences sharedPreferences;
    private LiveData<List<SqueakProfile>> mAllSqueakProfiles;
    private MutableLiveData<Integer> mSelectedSqueakProfileId;
    public Sha256Hash replyToHash;

    public CreateSqueakModel(Application application) {
        super(application);
        mProfileRepository = new SqueakProfileRepository(application);
        mSqueakRepository = new SqueakRepository(application);
        blockchainRepository = ElectrumBlockchainRepository.getRepository();
        blockchainRepository.setServer("electrumx-core.1209k.comm", 50001);
        mAllSqueakProfiles = mProfileRepository.getAllSqueakProfiles();
        mSelectedSqueakProfileId = new MutableLiveData<>();
        sharedPreferences = application.getSharedPreferences(
                SQUEAK_PROFILE_FILE_KEY, Context.MODE_PRIVATE);
        replyToHash = Sha256Hash.ZERO_HASH;

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

    LiveData<BlockInfo> getLatestBlock() {
        return blockchainRepository.getLatestBlock();
    }

    LiveData<CreateSqueakParams> getCreateSqueakParams() {
        return Transformations.switchMap(getSelectedSqueakProfile(), profile -> {
            Log.i(getClass().getName(), "Got selected profile: " + profile);
            return Transformations.map(getLatestBlock(), block -> {
                Log.i(getClass().getName(), "Got latest block: " + block);
                return new CreateSqueakParams(profile, replyToHash, block);
            });
        });
    }

    void insertSqueak(SqueakEntry squeakEntry) {
        mSqueakRepository.insert(squeakEntry);
    }

}
