package io.github.yzernik.squeakand;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.createprofile.CreateProfileFragment;

public class NewProfileActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        String profileName = getIntent().getStringExtra("name");

        Bundle bundle = new Bundle();
        bundle.putString("name", profileName);

        // Create new fragment and transaction
        Fragment newFragment = new CreateProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        newFragment.setArguments(bundle);
        transaction.replace(R.id.create_profile_fragment_frame, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

}
