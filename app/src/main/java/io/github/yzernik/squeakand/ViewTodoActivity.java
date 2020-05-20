package io.github.yzernik.squeakand;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.todo.ViewTodoFragment;

/**
 * Activity for entering a word.
 */

public class ViewTodoActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_todo);

        // Get the transferred data from source activity.
        String squeakHashStr = getIntent().getStringExtra("squeak_hash");
        Log.i(getCallingPackage(), "squeakHash in onCreate: " + squeakHashStr);

        Bundle bundle = new Bundle();
        bundle.putString("squeak_hash", squeakHashStr);
        // Create new fragment and transaction
        Fragment newFragment = new ViewTodoFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        newFragment.setArguments(bundle);
        // int currentContainerViewId = ((ViewGroup) getView().getParent()).getId();
        transaction.replace(R.id.view_todo_fragment_frame, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

}
