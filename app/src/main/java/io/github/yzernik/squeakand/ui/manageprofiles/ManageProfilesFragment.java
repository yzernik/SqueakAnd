package io.github.yzernik.squeakand.ui.manageprofiles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.yzernik.squeakand.NewProfileActivity;
import io.github.yzernik.squeakand.ProfileListAdapter;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.ViewContactActivity;


public class ManageProfilesFragment extends Fragment implements ProfileListAdapter.ClickListener{

    public static final int NEW_PROFILE_ACTIVITY_REQUEST_CODE = 1;

    private Spinner mProfilesSpinner;
    private Button mCreateProfileButton;

    private ManageProfilesModel manageProfilesModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_manage_profiles, container, false);

        final RecyclerView recyclerView = root.findViewById(R.id.profilesRecyclerView);
        final ProfileListAdapter adapter = new ProfileListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));


        mCreateProfileButton = root.findViewById(R.id.create_profile_button);

        manageProfilesModel = new ViewModelProvider(getActivity()).get(ManageProfilesModel.class);

        mCreateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Create profile button clicked");
                startActivityForResult(new Intent(getActivity(), NewProfileActivity.class), NEW_PROFILE_ACTIVITY_REQUEST_CODE);
            }
        });

/*        manageProfilesModel.getmAllSqueakProfiles().observe(getViewLifecycleOwner(), new Observer<List<SqueakProfile>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakProfile> squeakProfiles) {
                List<String> profileNames = squeakProfiles.stream()
                        .map(SqueakProfile::getName)
                        .collect(Collectors.toList());
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, profileNames);
                mProfilesSpinner.setAdapter(spinnerArrayAdapter);
            }
        });*/

        manageProfilesModel.getmAllSqueakSigningProfiles().observe(getViewLifecycleOwner(), new Observer<List<SqueakProfile>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakProfile> squeakProfiles) {
                // Update the cached copy of the profiles in the adapter.
                adapter.setProfiles(squeakProfiles);
            }
        });

        return root;
    }

    @Override
    public void handleItemClick(int id) {
        // TODO: go to profile activity
        Log.i(getTag(), "Clicked on profile id: " + id);
        startActivity(new Intent(getActivity(), ViewContactActivity.class).putExtra("profile_id", id));
    }

}
