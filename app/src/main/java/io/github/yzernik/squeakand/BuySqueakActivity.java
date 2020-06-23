package io.github.yzernik.squeakand;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.buysqueak.BuySqueakFragment;

public class BuySqueakActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_squeak);

        // Get the transferred data from source activity.
        String squeakHashStr = getIntent().getStringExtra("squeak_hash");
        Log.i(getCallingPackage(), "squeakHash in onCreate: " + squeakHashStr);

        Bundle bundle = new Bundle();
        bundle.putString("squeak_hash", squeakHashStr);
        // Create new fragment and transaction
        Fragment newFragment = new BuySqueakFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        newFragment.setArguments(bundle);
        transaction.replace(R.id.buy_squeak_fragment_frame, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
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
