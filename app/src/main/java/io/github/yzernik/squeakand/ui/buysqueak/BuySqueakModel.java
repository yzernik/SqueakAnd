package io.github.yzernik.squeakand.ui.buysqueak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import org.bitcoinj.core.Sha256Hash;


public class BuySqueakModel extends AndroidViewModel {

    public Sha256Hash squeakHash;

    public BuySqueakModel(Application application, Sha256Hash squeakHash) {
        super(application);
        this.squeakHash = squeakHash;
    }

}