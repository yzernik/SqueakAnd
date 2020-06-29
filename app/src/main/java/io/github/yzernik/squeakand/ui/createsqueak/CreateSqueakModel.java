package io.github.yzernik.squeakand.ui.createsqueak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.SqueakControllerRepository;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;
import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.blockchain.status.ElectrumDownloaderStatus;
import io.github.yzernik.squeakand.preferences.Preferences;
import io.github.yzernik.squeaklib.core.Squeak;

public class CreateSqueakModel extends AndroidViewModel {

    private SqueakProfileRepository mProfileRepository;
    private SqueakRepository mSqueakRepository;
    private SqueakControllerRepository mSqueakControllerRepository;
    private ElectrumBlockchainRepository blockchainRepository;
    private LiveData<List<SqueakProfile>> mAllSqueakSigningProfiles;
    private MutableLiveData<Integer> mSelectedSqueakProfileId;
    public Sha256Hash replyToHash;
    private Preferences preferences;

    public CreateSqueakModel(Application application, Sha256Hash replyToHash) {
        super(application);
        mProfileRepository = new SqueakProfileRepository(application);
        mSqueakRepository = SqueakRepository.getRepository(application);
        mSqueakControllerRepository = SqueakControllerRepository.getRepository(application);
        blockchainRepository = ElectrumBlockchainRepository.getRepository(application);
        mAllSqueakSigningProfiles = mProfileRepository.getAllSqueakSigningProfiles();
        mSelectedSqueakProfileId = new MutableLiveData<>();
        this.replyToHash = (replyToHash == null) ? Sha256Hash.ZERO_HASH : replyToHash;
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
                        return profile;
                    }
                }
                return null;
            });
        });
    }

    LiveData<BlockInfo> getLatestBlock() {
        return Transformations.map(getServerUpdate(), serverUpdate -> {
            return serverUpdate.getLatestBlockInfo();
        });
    }

    LiveData<ElectrumDownloaderStatus> getServerUpdate() {
        return blockchainRepository.getServerUpdate();
    }

    LiveData<CreateSqueakParams> getCreateSqueakParams() {
        return Transformations.switchMap(getSelectedSqueakProfile(), profile -> {
            return Transformations.map(getLatestBlock(), block -> {
                return new CreateSqueakParams(profile, replyToHash, block);
            });
        });
    }

    void insertSqueak(Squeak squeak, Block block) {
        mSqueakControllerRepository.insertWithBlock(squeak, block);
    }

    void uploadSqueak(Squeak squeak) {
        mSqueakControllerRepository.publishSqueak(squeak);
    }

    LiveData<SqueakEntryWithProfile> getReplyToSqueak() {
        return mSqueakRepository.getSqueak(replyToHash);
    }

}
