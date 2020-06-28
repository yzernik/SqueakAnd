package io.github.yzernik.squeakand.ui.lightningnode;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LightningNodeModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;

    private final String pubkey;
    private final String host;

    public LightningNodeModelFactory(@NonNull Application application, String pubkey, String host) {
        this.application = application;
        this.pubkey = pubkey;
        this.host = host;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LightningNodeModel(application, pubkey, host);
    }

}
