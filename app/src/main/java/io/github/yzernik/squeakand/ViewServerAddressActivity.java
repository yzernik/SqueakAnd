package io.github.yzernik.squeakand;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.viewserver.ViewServerAddressFragment;

public class ViewServerAddressActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_server_address);

        // Get the transferred data from source activity.
        String squeakServerAddress = getIntent().getStringExtra("squeak_server_address");
        Log.i(getCallingPackage(), "squeakServerAddress in onCreate: " + squeakServerAddress);

        Bundle bundle = new Bundle();
        bundle.putString("squeak_server_address", squeakServerAddress);
        // Create new fragment and transaction
        Fragment newFragment = new ViewServerAddressFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        newFragment.setArguments(bundle);
        // int currentContainerViewId = ((ViewGroup) getView().getParent()).getId();
        transaction.replace(R.id.view_server_address_fragment_frame, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

}
