package io.github.yzernik.squeakand.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import io.github.yzernik.squeakand.ManageProfilesActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;

public class SelectProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button mSelectProfileButton;
    private TextView mSelectedProfileText;
    private Button mManageProfilesButton;
    private TextView mSelectedProfileText2;
    private ArrayAdapter<SqueakProfile> adapter;

    private SelectProfileModel selectProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        selectProfileModel =
                ViewModelProviders.of(this).get(SelectProfileModel.class);
        View root = inflater.inflate(R.layout.fragment_select_profile, container, false);

        mSelectProfileButton = root.findViewById(R.id.select_profile_button);
        mSelectedProfileText = root.findViewById(R.id.selected_profile_text);
        mManageProfilesButton = root.findViewById(R.id.manage_profiles_button);
        mSelectedProfileText2 = root.findViewById(R.id.profile_name);

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
                }
            }
        });


        mManageProfilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Manage profiles button clicked");
                Intent intent = new Intent(getContext(), ManageProfilesActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SqueakProfile profile = (SqueakProfile) parent.getItemAtPosition(position);
        selectProfileModel.setSelectedSqueakProfileId(profile.profile_id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }

    /**
     * Update the display with the given profile
     * @param squeakProfile
     */
    private void updateDisplayedProfile(SqueakProfile squeakProfile) {
        Log.i(getTag(), "Updating SelectProfileFragment display with profile: " + squeakProfile);
        mSelectedProfileText.setText(squeakProfile.getName());
        mSelectedProfileText2.setText(squeakProfile.getName());
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
                Toast.makeText(getContext(), "Selected profile: " + selectedProfile.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
