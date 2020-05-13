package io.github.yzernik.squeakand.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.ManageProfilesActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SelectProfileActivity;
import io.github.yzernik.squeakand.SqueakProfile;

public class SelectProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button mSelectProfileButton;
    private TextView mSelectedProfileText;
    private Button mManageProfilesButton;


    private SelectProfileModel selectProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        selectProfileModel =
                ViewModelProviders.of(this).get(SelectProfileModel.class);
        View root = inflater.inflate(R.layout.fragment_select_profile, container, false);

        mSelectProfileButton = root.findViewById(R.id.select_profile_button);
        mSelectedProfileText = root.findViewById(R.id.selected_profile_text);
        mManageProfilesButton = root.findViewById(R.id.manage_profiles_button);

        selectProfileModel.getSelectedSqueakProfile().observe(getViewLifecycleOwner(), new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable final SqueakProfile squeakProfile) {
                // set the textview to show the currently selected profile.
                if (squeakProfile != null) {
                    mSelectedProfileText.setText(squeakProfile.getName());
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

}
