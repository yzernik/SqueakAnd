package io.github.yzernik.squeakand.ui.lightningnode;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LightningNodeChannelsModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;

    private final String pubkey;

    public LightningNodeChannelsModelFactory(@NonNull Application application, String pubkey) {
        this.application = application;
        this.pubkey = pubkey;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LightningNodeChannelsModel(application, pubkey);
    }

}
