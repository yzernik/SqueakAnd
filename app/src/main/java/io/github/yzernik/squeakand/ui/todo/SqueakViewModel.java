package io.github.yzernik.squeakand.ui.todo;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.Todo;
import io.github.yzernik.squeakand.TodoRepository;

public class SqueakViewModel extends AndroidViewModel {

    private SqueakRepository mRepository;
    // Using LiveData and caching what fetchTodoById returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    public SqueakViewModel(Application application) {
        super(application);
        mRepository = new SqueakRepository(application);
    }

    public LiveData<SqueakEntry> getSingleTodo(Sha256Hash hash) {
        return mRepository.getSqueak(hash);
    }

}