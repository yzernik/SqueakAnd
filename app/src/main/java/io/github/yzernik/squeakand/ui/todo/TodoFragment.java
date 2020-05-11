package io.github.yzernik.squeakand.ui.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.Todo;


public class TodoFragment extends Fragment {

    public static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;

    Spinner spinner;
    EditText inTitle, inDesc;
    Button btnDone, btnDelete;
    boolean isNewTodo = false;

    private String[] categories = {
            "Android",
            "iOS",
            "Kotlin",
            "Swift"
    };

    public ArrayList<String> spinnerList = new ArrayList<>(Arrays.asList(categories));

    private TodoViewModel todoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        int todoId = this.getArguments().getInt("todo_id");

        // Get a new or existing ViewModel from the ViewModelProvider.
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_todo, container, false);

        spinner = root.findViewById(R.id.spinner);
        inTitle = root.findViewById(R.id.inTitle);
        inDesc = root.findViewById(R.id.inDescription);
        btnDone = root.findViewById(R.id.btnDone);
        btnDelete = root.findViewById(R.id.btnDelete);

        todoViewModel.getSingleTodo(todoId).observe(getViewLifecycleOwner(), new Observer<Todo>() {
            @Override
            public void onChanged(@Nullable Todo todo) {
                System.out.println("Handling onChanged: " + todo);
                if (todo == null) {
                    return;
                }

                System.out.println("Setting layout to show todo: " + todo);

                inTitle.setText(todo.name);
                inDesc.setText(todo.description);
                spinner.setSelection(spinnerList.indexOf(todo.category));
            }
        });

        // TODO: Add button to create reply squeak.
        /*
        FloatingActionButton fab = root.findViewById(R.id.home_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewTodoActivity.class);
                startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);
            }
        });*/

        return root;
    }

    // TODO: handle activity results (create reply sqk, etc.)
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == NEW_TODO_ACTIVITY_REQUEST_CODE) {
                String todoInput = data.getStringExtra(NewTodoActivity.EXTRA_REPLY);
                Todo todo = new Todo(todoInput);
                todoViewModel.insert(todo);
            } else {
                Toast.makeText(getActivity(), "No action done by user", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}
