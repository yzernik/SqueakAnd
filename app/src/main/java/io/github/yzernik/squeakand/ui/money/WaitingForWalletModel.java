package io.github.yzernik.squeakand.ui.money;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.lnd.LndWalletStatus;

public class WaitingForWalletModel extends AndroidViewModel implements WalletBackupAndDeleter {

    private LndRepository lndRepository;
    private LiveData<LndWalletStatus> liveLndWalletStatus;

    private MutableLiveData<Boolean> liveIsWalletUnlocked = new MutableLiveData<>();

    public WaitingForWalletModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
        this.liveLndWalletStatus = lndRepository.getLndWalletStatus();
    }

    LiveData<Boolean> getLiveIsRpcReady() {
        return Transformations.map(liveLndWalletStatus, lndWalletStatus -> {
            return lndWalletStatus.isRpcReady();
        });
    }

    @Override
    public void deleteWallet() {
        lndRepository.deleteWallet();
    }

    @Override
    public String[] getWalletSeed() {
        return lndRepository.getWalletSeedWords();
    }

}
