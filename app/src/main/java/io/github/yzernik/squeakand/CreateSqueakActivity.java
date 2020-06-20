package io.github.yzernik.squeakand;

import android.os.Bundle;
import android.util.Log;

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

        // Get the transferred data from source activity.
        String replyToHashStr = getIntent().getStringExtra("reply_to_hash");
        Log.i(getCallingPackage(), "replyToHashStr in onCreate: " + replyToHashStr);

        Bundle bundle = new Bundle();
        bundle.putString("reply_to_hash", replyToHashStr);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment createSqueakFragment = new CreateSqueakFragment();
        createSqueakFragment.setArguments(bundle);
        transaction.replace(R.id.create_squeak_fragment_frame, createSqueakFragment);
        transaction.commit();
    }
}
