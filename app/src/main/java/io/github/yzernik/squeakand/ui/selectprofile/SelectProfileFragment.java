package io.github.yzernik.squeakand.ui.selectprofile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import io.github.yzernik.squeakand.ManageProfilesActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;

public class SelectProfileFragment extends Fragment {

    private Button mSelectProfileButton;
    private Button mManageProfilesButton;
    private TextView mSelectedProfileText2;
    private TextView mSelectedProfileAddress;

    private SelectProfileModel selectProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_select_profile, container, false);

        mSelectProfileButton = root.findViewById(R.id.select_profile_button);
        mManageProfilesButton = root.findViewById(R.id.manage_profiles_button);
        mSelectedProfileText2 = root.findViewById(R.id.profile_name);

        mSelectedProfileAddress = root.findViewById(R.id.profile_address);

        selectProfileModel = new ViewModelProvider(getActivity()).get(SelectProfileModel.class);

        selectProfileModel.getmAllSqueakProfiles().observe(getViewLifecycleOwner(), new Observer<List<SqueakProfile>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakProfile> profiles) {
                mSelectProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(getTag(),"Select profile button clicked");
                        showAlertDialog(profiles);
                    }
                });
            }
        });

        selectProfileModel.getSelectedSqueakProfile().observe(getViewLifecycleOwner(), new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable final SqueakProfile squeakProfile) {
                Log.i(getTag(),"Got selected from from observe: " + squeakProfile);
                // set the textview to show the currently selected profile.
                if (squeakProfile != null) {
                    updateDisplayedProfile(squeakProfile);
                    Log.i(getTag(), "Updated sharedviewmodel.");
                }
            }
        });


        mManageProfilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Manage profiles button clicked");
                Intent intent = new Intent(getActivity(), ManageProfilesActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    /**
     * Update the display with the given profile
     * @param squeakProfile
     */
    private void updateDisplayedProfile(SqueakProfile squeakProfile) {
        Log.i(getTag(), "Updating SelectProfileFragment display with profile: " + squeakProfile);
        mSelectedProfileText2.setText(squeakProfile.getName());
        mSelectedProfileAddress.setText(squeakProfile.getAddress());
    }

    /**
     * Show the alert dialog for selecting a profile.
     * @param profiles
     */
    private void showAlertDialog(List<SqueakProfile> profiles) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose a profile");
        // add a list
        ArrayList<String> displayValues=new ArrayList<>();
        for (SqueakProfile profile : profiles) {
            displayValues.add(profile.getName());
        }
        String[] displayValuesArr = displayValues.toArray(new String[displayValues.size()]);
        builder.setItems(displayValuesArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SqueakProfile selectedProfile = profiles.get(which);
                selectProfileModel.setSelectedSqueakProfileId(selectedProfile.getProfileId());
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}