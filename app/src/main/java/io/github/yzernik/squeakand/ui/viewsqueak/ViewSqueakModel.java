package io.github.yzernik.squeakand.ui.viewsqueak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakRepository;

public class ViewSqueakModel extends AndroidViewModel {

    private SqueakRepository mRepository;
    // Using LiveData and caching what fetchTodoById returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    public ViewSqueakModel(Application application) {
        super(application);
        mRepository = SqueakRepository.getRepository(application);
    }

    public LiveData<SqueakEntryWithProfile> getSingleTodo(Sha256Hash hash) {
        return mRepository.getSqueak(hash);
    }

}