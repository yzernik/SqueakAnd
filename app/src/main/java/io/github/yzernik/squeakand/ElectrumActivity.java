package io.github.yzernik.squeakand;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.electrum.ElectrumFragment;

public class ElectrumActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electrum);

        // Create new fragment and transaction
        Fragment newFragment = new ElectrumFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.blockchain_fragment_frame, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }


}
