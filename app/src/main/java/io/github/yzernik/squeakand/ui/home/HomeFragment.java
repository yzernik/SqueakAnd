package io.github.yzernik.squeakand.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.github.yzernik.squeakand.MainActivity;
import io.github.yzernik.squeakand.NewTodoActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.Todo;
import io.github.yzernik.squeakand.TodoListAdapter;
import io.github.yzernik.squeakand.ViewTodoActivity;
import io.github.yzernik.squeakand.ui.todo.TodoFragment;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements TodoListAdapter.ClickListener {

    public static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_TODO_REQUEST_CODE = 300;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        final TodoListAdapter adapter = new TodoListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        // Get a new or existing ViewModel from the ViewModelProvider.
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedTodos.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        homeViewModel.getAllTodos().observe(getViewLifecycleOwner(), new Observer<List<Todo>>() {
            @Override
            public void onChanged(@Nullable final List<Todo> todos) {
                // Update the cached copy of the todos in the adapter.
                adapter.setTodos(todos);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.home_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewTodoActivity.class);
                startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == NEW_TODO_ACTIVITY_REQUEST_CODE) {
                String todoInput = data.getStringExtra(NewTodoActivity.EXTRA_REPLY);
                Todo todo = new Todo(todoInput);
                homeViewModel.insert(todo);
            } else {
                Toast.makeText(getActivity(), "No action done by user", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void handleItemClick(int id) {
        startActivityForResult(new Intent(getActivity(), ViewTodoActivity.class).putExtra("id", id), UPDATE_TODO_REQUEST_CODE);
        // startActivityForResult(new Intent(getActivity(), TodoFragment.class).putExtra("id", id), UPDATE_TODO_REQUEST_CODE);
/*
        Bundle bundle = new Bundle();
        bundle.putInt("todo_id", id);

        // Create new fragment and transaction
        Fragment newFragment = new TodoFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        newFragment.setArguments(bundle);

        int currentContainerViewId = ((ViewGroup)getView().getParent()).getId();
        transaction.replace(currentContainerViewId, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();*/
    }
}
