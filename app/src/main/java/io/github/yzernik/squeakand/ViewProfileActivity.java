package io.github.yzernik.squeakand;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.viewprofile.ViewProfileFragment;

public class ViewProfileActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);


        // Get the transferred data from source activity.
        int profileId = getIntent().getIntExtra("profile_id", -1);
        Log.i(getCallingPackage(), "profileId in onCreate: " + profileId);

        Bundle bundle = new Bundle();
        bundle.putInt("profile_id", profileId);
        // Create new fragment and transaction
        Fragment newFragment = new ViewProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        newFragment.setArguments(bundle);
        // int currentContainerViewId = ((ViewGroup) getView().getParent()).getId();
        transaction.replace(R.id.view_profile_fragment_frame, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

}
