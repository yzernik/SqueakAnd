package io.github.yzernik.squeakand;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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

    // TODO: get the id from the class variable, not a method parameter.
    public LiveData<Todo> getSingleTodo(int id) {
        return mRepository.getTodo(id);
    }

}