package io.github.yzernik.squeakand;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.createcontact.CreateContactFragment;

public class NewContactActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        // Get the transferred data from source activity.
        String squeakAddress = getIntent().getStringExtra("squeak_address");
        Log.i(getCallingPackage(), "squeakAddress in onCreate: " + squeakAddress);

        Bundle bundle = new Bundle();
        bundle.putString("squeak_address", squeakAddress);

        // Create new fragment and transaction
        Fragment newFragment = new CreateContactFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        newFragment.setArguments(bundle);
        transaction.replace(R.id.create_contact_fragment_frame, newFragment);
        transaction.commit();
    }

}
