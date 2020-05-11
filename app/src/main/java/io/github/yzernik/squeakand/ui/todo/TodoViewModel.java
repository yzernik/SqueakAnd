package io.github.yzernik.squeakand.ui.todo;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.Todo;
import io.github.yzernik.squeakand.TodoRepository;

public class TodoViewModel extends AndroidViewModel {

    private TodoRepository mRepository;
    // Using LiveData and caching what fetchTodoById returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<Todo> mSingleTodo;

    public TodoViewModel(Application application) {
        super(application);
        mRepository = new TodoRepository(application);
        // TODO: use correct id.
        mSingleTodo = mRepository.getTodo(12345);
    }

    LiveData<Todo> getSingleTodo() {
        return mSingleTodo;
    }

}