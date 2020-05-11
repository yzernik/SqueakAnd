package io.github.yzernik.squeakand.ui.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.Todo;


public class TodoFragment extends Fragment {

    public static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;

    TextView txtName;
    TextView txtNo;
    TextView txtDesc;
    TextView txtCategory;
    CardView cardView;

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
        View root = inflater.inflate(R.layout.recyclerview_item_layout, container, false);

        txtNo = root.findViewById(R.id.txtNo);
        txtName = root.findViewById(R.id.txtName);
        txtDesc = root.findViewById(R.id.txtDesc);
        txtCategory = root.findViewById(R.id.txtCategory);
        cardView = root.findViewById(R.id.cardView);

        todoViewModel.getSingleTodo(todoId).observe(getViewLifecycleOwner(), new Observer<Todo>() {
            @Override
            public void onChanged(@Nullable Todo todo) {
                System.out.println("Handling onChanged: " + todo);
                if (todo == null) {
                    return;
                }

                System.out.println("Setting layout to show todo: " + todo);
                txtName.setText(todo.getName());
                txtNo.setText("#" + String.valueOf(todo.todo_id));
                txtDesc.setText(todo.description);
                txtCategory.setText(todo.category);
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
