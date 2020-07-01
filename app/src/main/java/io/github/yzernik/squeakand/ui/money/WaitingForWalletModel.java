package io.github.yzernik.squeakand.ui.money;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.github.yzernik.squeakand.lnd.LndRepository;

public class WaitingForWalletModel extends AndroidViewModel {

    private LndRepository lndRepository;

    private MutableLiveData<Boolean> liveIsWalletUnlocked = new MutableLiveData<>();

    public WaitingForWalletModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
    }

    LiveData<Boolean> getLiveIsWalletUnlocked() {
        return liveIsWalletUnlocked;
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
