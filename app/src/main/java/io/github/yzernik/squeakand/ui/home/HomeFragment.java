package io.github.yzernik.squeakand.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.NewTodoActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakListAdapter;


public class HomeFragment extends Fragment implements SqueakListAdapter.ClickListener {

    public static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_TODO_REQUEST_CODE = 300;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        final SqueakListAdapter adapter = new SqueakListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        // Get a new or existing ViewModel from the ViewModelProvider.
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedTodos.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        homeViewModel.getAllSqueaks().observe(getViewLifecycleOwner(), new Observer<List<SqueakEntry>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakEntry> squeakEntries) {
                // Update the cached copy of the squeaks in the adapter.
                adapter.setSqueaks(squeakEntries);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.home_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewTodoActivity.class);
                startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(getTag(), "Got activity result requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(getTag(), "Called super.onActivityResult...");

/*        if (resultCode == RESULT_OK) {

            if (requestCode == NEW_TODO_ACTIVITY_REQUEST_CODE) {
                String todoInput = data.getStringExtra(NewTodoActivity.EXTRA_REPLY);
                Todo todo = new Todo(todoInput);
                homeViewModel.insert(todo);
            } else {
                Toast.makeText(getActivity(), "No action done by user", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    @Override
    public void handleItemClick(Sha256Hash hash) {
        // startActivityForResult(new Intent(getActivity(), ViewTodoActivity.class).putExtra("id", id), UPDATE_TODO_REQUEST_CODE);
        // TODO: Go to the squeak view activity for the hash
    }
}
