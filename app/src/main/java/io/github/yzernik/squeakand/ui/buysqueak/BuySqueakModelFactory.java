package io.github.yzernik.squeakand.ui.buysqueak;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.bitcoinj.core.Sha256Hash;

public class BuySqueakModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;

    private final Sha256Hash replyToHash;

    public BuySqueakModelFactory(@NonNull Application application, Sha256Hash replyToHash) {
        this.application = application;
        this.replyToHash = replyToHash;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BuySqueakModel(application, replyToHash);
    }

}
