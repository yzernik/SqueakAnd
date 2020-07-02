package io.github.yzernik.squeakand.ui.viewserver;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.server.SqueakServerAddress;

public class ViewServerAddressModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;

    private final SqueakServerAddress serverAddress;

    public ViewServerAddressModelFactory(@NonNull Application application, SqueakServerAddress serverAddress) {
        this.application = application;
        this.serverAddress = serverAddress;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ViewServerAddressModel(application, serverAddress);
    }

}
