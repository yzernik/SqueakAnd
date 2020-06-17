package io.github.yzernik.squeakand.ui.money;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.github.yzernik.squeakand.lnd.LndRepository;

public class MoneyViewModel  extends AndroidViewModel {

    private LndRepository lndRepository;

    public MoneyViewModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
    }

    void initWallet() {
        lndRepository.initWallet();
    }

    void getInfo() {
        lndRepository.getInfo();
    }

}
