package io.github.yzernik.squeakand.ui.money;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.lnd.LndWalletStatus;

public class NoWalletInitializedModel extends AndroidViewModel {

    private LndRepository lndRepository;
    private LiveData<LndWalletStatus> liveLndWalletStatus;

    public NoWalletInitializedModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
        this.liveLndWalletStatus = lndRepository.getLndWalletStatus();
    }

    LiveData<LndWalletStatus> getLiveLndWalletStatus() {
        return liveLndWalletStatus;
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
