package io.github.yzernik.squeakand.ui.viewsqueak;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.bitcoinj.core.Sha256Hash;

public class ViewSqueakModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;

    private final Sha256Hash squeakHash;

    public ViewSqueakModelFactory(@NonNull Application application, Sha256Hash squeakHash) {
        this.application = application;
        this.squeakHash = squeakHash;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ViewSqueakModel(application, squeakHash);
    }

}
