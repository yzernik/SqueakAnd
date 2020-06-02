package io.github.yzernik.squeakand.ui.viewcontact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;

public class ViewContactFragment extends Fragment {

    private TextView mContactNameText;
    private TextView mContactAddressText;
    private Button mRenameContactButton;

    private ViewContactModel viewContactModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_contact, container, false);

        int profileId = this.getArguments().getInt("profile_id", -1);

        mContactNameText = root.findViewById(R.id.view_contact_show_name);
        mContactAddressText = root.findViewById(R.id.view_contact_show_address);
        mRenameContactButton = root.findViewById(R.id.view_contact_rename_button);

        viewContactModel = new ViewModelProvider(getActivity()).get(ViewContactModel.class);

        viewContactModel.getSqueakContactProfile(profileId).observe(getViewLifecycleOwner(), new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable SqueakProfile squeakProfile) {
                if (squeakProfile == null) {
                    return;
                }

                mContactNameText.setText(squeakProfile.getName());
                mContactAddressText.setText(squeakProfile.getAddress());


                mRenameContactButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRenameAlertDialog(inflater, squeakProfile);
                    }
                });
            }
        });



        return root;
    }

    private void showRenameAlertDialog(LayoutInflater inflater, SqueakProfile squeakProfile) {
        final View view = inflater.inflate(R.layout.dialog_rename_profile, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Rename contact");

        final EditText newNameText = (EditText) view.findViewById(R.id.dialog_rename_profile_new_name_input);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Rename",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String newName = newNameText.getText().toString();
                        if (newName == null || newName.isEmpty()) {
                            return;
                        }
                        squeakProfile.name = newName;
                        Log.i(getTag(), "New profile class: " + squeakProfile);
                        viewContactModel.updateProfile(squeakProfile);
                        Log.i(getTag(), "Setting new name for profile to: " + newName);
                    }
                });

/*        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing.
            }
        });*/

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setView(view);
        alertDialog.show();
    }


}
