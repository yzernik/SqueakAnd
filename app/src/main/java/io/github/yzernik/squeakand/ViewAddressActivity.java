package io.github.yzernik.squeakand;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.viewaddress.ViewAddressFragment;

public class ViewAddressActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_address);


        // Get the transferred data from source activity.
        String squeakAddress = getIntent().getStringExtra("squeak_address");
        Log.i(getCallingPackage(), "squeakAddress in onCreate: " + squeakAddress);

        Bundle bundle = new Bundle();
        bundle.putString("squeak_address", squeakAddress);
        // Create new fragment and transaction
        Fragment newFragment = new ViewAddressFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        newFragment.setArguments(bundle);
        // int currentContainerViewId = ((ViewGroup) getView().getParent()).getId();
        transaction.replace(R.id.view_address_fragment_frame, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

}
