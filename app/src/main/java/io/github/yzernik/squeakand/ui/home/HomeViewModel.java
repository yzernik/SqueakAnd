package io.github.yzernik.squeakand.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.Todo;
import io.github.yzernik.squeakand.TodoRepository;

public class HomeViewModel extends AndroidViewModel {

    private SqueakRepository mSqueakRepository;
    private TodoRepository mRepository;
    // Using LiveData and caching what getAlphabetizedTodos returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Todo>> mAllTodos;
    private LiveData<List<SqueakEntry>> mAllSqueaks;

    public HomeViewModel(Application application) {
        super(application);
        mSqueakRepository = new SqueakRepository(application);
        mRepository = new TodoRepository(application);
        mAllTodos = mRepository.getAllTodos();
        mAllSqueaks = mSqueakRepository.getAllSqueaks();
    }

    LiveData<List<Todo>> getAllTodos() {
        return mAllTodos;
    }

    LiveData<List<SqueakEntry>> getAllSqueaks() {
        return mAllSqueaks;
    }

    void insert(Todo todo) {
        mRepository.insert(todo);
    }
}