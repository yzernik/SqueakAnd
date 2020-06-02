package io.github.yzernik.squeakand.ui.createsqueak;

import android.app.Application;
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
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.blockchain.ServerUpdate;
import io.github.yzernik.squeakand.preferences.Preferences;

public class CreateSqueakModel extends AndroidViewModel {

    private SqueakProfileRepository mProfileRepository;
    private SqueakRepository mSqueakRepository;
    private ElectrumBlockchainRepository blockchainRepository;
    private LiveData<List<SqueakProfile>> mAllSqueakSigningProfiles;
    private MutableLiveData<Integer> mSelectedSqueakProfileId;
    public Sha256Hash replyToHash;
    private Preferences preferences;

    public CreateSqueakModel(Application application) {
        super(application);
        mProfileRepository = new SqueakProfileRepository(application);
        mSqueakRepository = new SqueakRepository(application);
        blockchainRepository = ElectrumBlockchainRepository.getRepository(application);
        mAllSqueakSigningProfiles = mProfileRepository.getAllSqueakSigningProfiles();
        mSelectedSqueakProfileId = new MutableLiveData<>();
        replyToHash = Sha256Hash.ZERO_HASH;
        preferences = new Preferences(application);

        // Set the initial value of squeakprofile id
        loadSelectedSqueakProfileId();
    }

    LiveData<List<SqueakProfile>> getmAllSqueakSigningProfiles() {
        return mAllSqueakSigningProfiles;
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
        return preferences.getProfileId();
    }

    private void saveSelectedProfileId(int squeakProfileId) {
        preferences.saveSelectedProfileId(squeakProfileId);
    }

    LiveData<SqueakProfile> getSelectedSqueakProfile() {
        return Transformations.switchMap(mAllSqueakSigningProfiles, profiles -> {
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
        return Transformations.map(getServerUpdate(), serverUpdate -> {
            return serverUpdate.getBlockInfo();
        });
    }

    LiveData<ServerUpdate> getServerUpdate() {
        return blockchainRepository.getServerUpdate();
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
