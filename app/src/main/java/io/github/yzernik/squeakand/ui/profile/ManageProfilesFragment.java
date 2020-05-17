package io.github.yzernik.squeakand.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.NewProfileActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.ViewTodoActivity;
import io.github.yzernik.squeakand.ui.createprofile.CreateProfileFragment;


public class ManageProfilesFragment extends Fragment {

    public static final int NEW_PROFILE_ACTIVITY_REQUEST_CODE = 1;

    private Spinner mProfilesSpinner;
    private Button mCreateProfileButton;

    private SelectProfileModel selectProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        selectProfileModel =
                ViewModelProviders.of(this).get(SelectProfileModel.class);
        View root = inflater.inflate(R.layout.fragment_manage_profiles, container, false);

        mCreateProfileButton = root.findViewById(R.id.create_profile_button);
        mProfilesSpinner = root.findViewById(R.id.profiles_spinner);

        mCreateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Create profile button clicked");
                // create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Create Profile");
                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.dialog_new_profile, null);
                builder.setView(customLayout);
                // add a button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send data from the AlertDialog to the Activity
                        EditText editText = customLayout.findViewById(R.id.editText);
                        handleNewProfileDialogData(editText.getText().toString());
                    }
                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        selectProfileModel.getmAllSqueakProfiles().observe(getViewLifecycleOwner(), new Observer<List<SqueakProfile>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakProfile> squeakProfiles) {
                List<String> profileNames = squeakProfiles.stream()
                        .map(SqueakProfile::getName)
                        .collect(Collectors.toList());
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, profileNames);
                mProfilesSpinner.setAdapter(spinnerArrayAdapter);
            }
        });

        return root;
    }

    // do something with the data coming from the AlertDialog
    private void handleNewProfileDialogData(String data) {
        //Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
        //SqueakProfile squeakProfile = new SqueakProfile(data);
        //selectProfileModel.insert(squeakProfile);

        startActivityForResult(new Intent(getActivity(), NewProfileActivity.class).putExtra("name", data), NEW_PROFILE_ACTIVITY_REQUEST_CODE);
    }

}
