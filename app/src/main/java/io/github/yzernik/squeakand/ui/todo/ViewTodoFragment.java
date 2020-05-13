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
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.Todo;

public class ViewTodoFragment extends Fragment {

    TextView txtName;
    TextView txtNo;
    TextView txtDesc;
    TextView txtCategory;
    CardView cardView;

    // private EditText mEditTodoView;
    private TodoViewModel todoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        todoViewModel =
                ViewModelProviders.of(this).get(TodoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_todo, container, false);

        int todoId = 0;
        Bundle arguments = getArguments();
        if (arguments != null) {
            todoId = this.getArguments().getInt("todo_id");
        }

        // Get a new or existing ViewModel from the ViewModelProvider.
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        txtNo = root.findViewById(R.id.txtNo);
        txtName = root.findViewById(R.id.txtName);
        txtDesc = root.findViewById(R.id.txtDesc);
        txtCategory = root.findViewById(R.id.txtCategory);
        cardView = root.findViewById(R.id.cardView);

        todoViewModel.getSingleTodo(todoId).observe(getViewLifecycleOwner(), new Observer<Todo>() {
            @Override
            public void onChanged(@Nullable Todo todo) {
                if (todo == null) {
                    return;
                }

                txtName.setText(todo.getName());
                txtNo.setText("#" + String.valueOf(todo.todo_id));
                txtDesc.setText(todo.description);
                txtCategory.setText(todo.category);
            }
        });

        return root;
    }

}
