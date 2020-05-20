package io.github.yzernik.squeakand;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.createsqueak.CreateSqueakFragment;

/**
 * Activity for creating a squeak.
 */

public class CreateSqueakActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_squeak);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment createTodoFragment = new CreateSqueakFragment();
        transaction.replace(R.id.create_squeak_fragment_frame, createTodoFragment);
        transaction.commit();
    }
}
