package io.github.yzernik.squeakand.ui.money;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.github.yzernik.squeakand.lnd.LndRepository;

public class NoWalletModel extends AndroidViewModel {

    private LndRepository lndRepository;

    private MutableLiveData<Boolean> liveIsWalletUnlocked = new MutableLiveData<>();
    private MutableLiveData<Boolean> liveHasWallet = new MutableLiveData<>();

    public NoWalletModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
        refreshWalletInfo();
    }

    LiveData<Boolean> getLiveIsWalletUnlocked() {
        return liveIsWalletUnlocked;
    }

    LiveData<Boolean> getLiveHasWallet() {
        return liveHasWallet;
    }

    private void refreshWalletInfo() {
        boolean hasWallet = lndRepository.hasWallet();
        boolean isWalletUnlocked = lndRepository.isWalletUnlocked();
        liveHasWallet.setValue(hasWallet);
        liveIsWalletUnlocked.setValue(isWalletUnlocked);
    }



}
