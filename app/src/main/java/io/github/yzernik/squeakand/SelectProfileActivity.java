package io.github.yzernik.squeakand;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SelectProfileActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);

/*        Bundle bundle = new Bundle();
        // Create new fragment and transaction
        Fragment newFragment = new SelectProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        newFragment.setArguments(bundle);
        // int currentContainerViewId = ((ViewGroup) getView().getParent()).getId();
        transaction.replace(R.id.select_profile_fragment, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();*/
    }

}
