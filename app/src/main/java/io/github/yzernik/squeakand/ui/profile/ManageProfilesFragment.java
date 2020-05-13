package io.github.yzernik.squeakand.ui.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;


public class ManageProfilesFragment extends Fragment {

    private Button mCreateProfileButton;

    private SelectProfileModel selectProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        selectProfileModel =
                ViewModelProviders.of(this).get(SelectProfileModel.class);
        View root = inflater.inflate(R.layout.fragment_manage_profiles, container, false);

        mCreateProfileButton = root.findViewById(R.id.create_profile_button);

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

        return root;
    }

    // do something with the data coming from the AlertDialog
    private void handleNewProfileDialogData(String data) {
        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
        SqueakProfile squeakProfile = new SqueakProfile(data);
        selectProfileModel.insert(squeakProfile);
    }

}
