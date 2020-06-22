package io.github.yzernik.squeakand.ui.viewsqueak;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakRepository;

public class ViewSqueakModel extends AndroidViewModel {

    private Sha256Hash squeakHash;

    private SqueakRepository mRepository;
    // Using LiveData and caching what fetchTodoById returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    public ViewSqueakModel(Application application, Sha256Hash squeakHash) {
        super(application);
        mRepository = SqueakRepository.getRepository(application);
        this.squeakHash = squeakHash;
    }

    public LiveData<SqueakEntryWithProfile> getSingleTodo() {
        return mRepository.getSqueak(squeakHash);
    }

    public LiveData<List<SqueakEntryWithProfile>> getThreadAncestorSqueaks() {
        return mRepository.getThreadAncestorSqueaks(squeakHash);
    }

}