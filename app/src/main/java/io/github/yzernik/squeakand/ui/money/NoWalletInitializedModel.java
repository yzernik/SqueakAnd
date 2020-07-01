package io.github.yzernik.squeakand.ui.money;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.github.yzernik.squeakand.lnd.LndRepository;

public class NoWalletInitializedModel extends AndroidViewModel {

    private LndRepository lndRepository;

    private MutableLiveData<Boolean> liveHasWallet = new MutableLiveData<>();

    public NoWalletInitializedModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
        refreshHasWallet();
    }

    LiveData<Boolean> getLiveHasWallet() {
        return liveHasWallet;
    }

    void refreshHasWallet() {
        boolean hasWallet = lndRepository.hasWallet();
        liveHasWallet.postValue(hasWallet);
    }

    public void createWallet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lndRepository.initWallet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
