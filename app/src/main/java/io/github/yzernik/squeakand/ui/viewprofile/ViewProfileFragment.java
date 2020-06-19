package io.github.yzernik.squeakand.ui.viewprofile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;

import static org.bitcoinj.core.Utils.HEX;

public class ViewProfileFragment extends Fragment {

    private TextView mProfileNameText;
    private TextView mProfileAddressText;
    private Switch mFollowingSwitch;
    private Button mExportProfileButton;
    private Button mRenameProfileButton;
    private Button mDeleteProfileButton;

    private ViewProfileModel viewProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_profile, container, false);

        int profileId = this.getArguments().getInt("profile_id", -1);

        mProfileNameText = root.findViewById(R.id.view_profile_show_name);
        mProfileAddressText = root.findViewById(R.id.view_profile_show_address);
        mFollowingSwitch = root.findViewById(R.id.view_profile_following_switch);
        mExportProfileButton = root.findViewById(R.id.view_profile_export_button);
        mRenameProfileButton = root.findViewById(R.id.view_profile_rename_button);
        mDeleteProfileButton = root.findViewById(R.id.view_profile_delete_button);

        viewProfileModel = new ViewModelProvider(getActivity()).get(ViewProfileModel.class);

        viewProfileModel.getSqueakProfile(profileId).observe(getViewLifecycleOwner(), new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable SqueakProfile squeakProfile) {
                if (squeakProfile == null) {
                    return;
                }

                mProfileNameText.setText(squeakProfile.getName());
                mProfileAddressText.setText(squeakProfile.getAddress());

                mFollowingSwitch.setChecked(squeakProfile.downloadEnabled);
                mFollowingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        squeakProfile.downloadEnabled = isChecked;
                        Log.i(getTag(), "New profile class: " + squeakProfile);
                        viewProfileModel.updateProfile(squeakProfile);
                    }
                });

                // Setup the export button.
                if (squeakProfile.isSigningProfile()) {
                    mExportProfileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showExportAlertDialog(inflater, squeakProfile);
                        }
                    });
                    mExportProfileButton.setVisibility(View.VISIBLE);
                }

                // Setup the rename button.
                mRenameProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRenameAlertDialog(inflater, squeakProfile);
                    }
                });

                // Setup the delete button.
                mDeleteProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteAlertDialog(inflater, squeakProfile);
                        // dialog.dismiss();
                        // getActivity().finish();
                    }
                });
            }
        });



        return root;
    }

    private void showRenameAlertDialog(LayoutInflater inflater, SqueakProfile squeakProfile) {
        final View view = inflater.inflate(R.layout.dialog_rename_profile, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Rename profile");

        final EditText newNameText = (EditText) view.findViewById(R.id.dialog_rename_profile_new_name_input);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Rename",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = newNameText.getText().toString();
                        if (newName == null || newName.isEmpty()) {
                            return;
                        }
                        squeakProfile.name = newName;
                        Log.i(getTag(), "New profile class: " + squeakProfile);
                        viewProfileModel.updateProfile(squeakProfile);
                        Log.i(getTag(), "Setting new name for profile to: " + newName);
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setView(view);
        alertDialog.show();
    }

    private void showDeleteAlertDialog(LayoutInflater inflater, SqueakProfile squeakProfile) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Delete profile");
        alertDialog.setMessage("Are you sure you want to delete this profile?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(getTag(), "Deleting profile: " + squeakProfile);
                        viewProfileModel.deleteProfile(squeakProfile);
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    private void showExportAlertDialog(LayoutInflater inflater, SqueakProfile squeakProfile) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Export private key");
        String msg = String.format("Are you sure you want to show the private key for %s?", squeakProfile.getName());
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Show",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(getTag(), "Showing private key for profile: " + squeakProfile);
                        dialog.dismiss();

                        // Show the private key
                        showPrivateKeyAlertDialog(inflater, squeakProfile);
                    }
                });


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    private void showPrivateKeyAlertDialog(LayoutInflater inflater, SqueakProfile squeakProfile) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Private key");
        byte[] privKeyBytes = squeakProfile.getKeyPair().getEcKey().getPrivKeyBytes();
        String privKeyString = HEX.encode(privKeyBytes);
        String msg = String.format("Do not share this private key with anyone: %s", privKeyString);
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }


}
