package io.github.yzernik.squeakand.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.Todo;
import io.github.yzernik.squeakand.TodoRepository;

public class HomeViewModel extends AndroidViewModel {

    private TodoRepository mRepository;
    // Using LiveData and caching what getAlphabetizedTodos returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Todo>> mAllTodos;

    public HomeViewModel(Application application) {
        super(application);
        mRepository = new TodoRepository(application);
        mAllTodos = mRepository.getAllTodos();
    }

    LiveData<List<Todo>> getAllTodos() {
        return mAllTodos;
    }

    void insert(Todo todo) {
        mRepository.insert(todo);
    }
}