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
    private MutableLiveData<Boolean> liveIsButtonClicked = new MutableLiveData<>();

    public NoWalletModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
        liveIsButtonClicked.setValue(false);
    }

    LiveData<Boolean> getLiveIsWalletUnlocked() {
        return liveIsWalletUnlocked;
    }

    LiveData<Boolean> getLiveHasWallet() {
        return liveHasWallet;
    }

    LiveData<Boolean> getLiveIsButtonClicked() {
        return liveIsButtonClicked;
    }

    public void buttonClicked() {
        liveIsButtonClicked.setValue(true);
    }

    public void waitForWalletUnlocked() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lndRepository.waitForWalletUnlocked();
                    liveIsWalletUnlocked.postValue(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
