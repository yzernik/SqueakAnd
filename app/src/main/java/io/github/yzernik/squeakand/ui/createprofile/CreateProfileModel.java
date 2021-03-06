package io.github.yzernik.squeakand.ui.createprofile;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.bitcoinj.core.ECKey;

import java.util.List;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileRepository;
import io.github.yzernik.squeaklib.core.Signing;

public class CreateProfileModel  extends AndroidViewModel {

    private SqueakProfileRepository mRepository;
    private LiveData<List<SqueakProfile>> mAllSqueakProfiles;
    // private MutableLiveData<KeyPair> mKeyPair;
    private MutableLiveData<ECKey> mECKey;

    public CreateProfileModel(Application application) {
        super(application);
        mRepository = new SqueakProfileRepository(application);
        mAllSqueakProfiles = mRepository.getAllSqueakProfiles();
        mECKey = new MutableLiveData<>();
        mECKey.setValue(null);
    }

    void setmKeyPair(ECKey ecKey) {
        this.mECKey.setValue(ecKey);
    }

    LiveData<List<SqueakProfile>> getmAllSqueakProfiles() {
        return mAllSqueakProfiles;
    }

    void insert(SqueakProfile squeakProfile) {
        mRepository.insert(squeakProfile);
    }

    LiveData<Signing.BitcoinjKeyPair> getKeyPair() {
        return Transformations.map(mECKey, key -> {
            if (key == null) {
                return null;
            }
            return new Signing.BitcoinjKeyPair(key);
        });
    }

}
