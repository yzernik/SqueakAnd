package io.github.yzernik.squeakand;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.lightningnode.LightningNodeFragment;
import io.github.yzernik.squeakand.ui.money.NoWalletInitializedFragment;

public class LightningNodeActivity  extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightning_node);

        // Get the transferred data from source activity.
        String pubkey = getIntent().getStringExtra("pubkey");
        String host = getIntent().getStringExtra("host");
        Log.i(getCallingPackage(), "pubkey in onCreate: " + pubkey);
        Log.i(getCallingPackage(), "host in onCreate: " + host);

        Bundle bundle = new Bundle();
        bundle.putString("pubkey", pubkey);
        bundle.putString("host", host);
        Fragment lightningNodeFragment = new LightningNodeFragment();
        lightningNodeFragment.setArguments(bundle);
        Fragment newFragment = new NoWalletInitializedFragment(lightningNodeFragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.lightning_node_fragment_frame, newFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
