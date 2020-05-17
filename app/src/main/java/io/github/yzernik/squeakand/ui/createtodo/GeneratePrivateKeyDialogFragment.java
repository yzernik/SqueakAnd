package io.github.yzernik.squeakand.ui.createtodo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import io.github.yzernik.squeakand.R;


public class GeneratePrivateKeyDialogFragment extends DialogFragment {

    private String profileName;
    private GeneratePrivateKeyNoticeDialogListener listener;

    public GeneratePrivateKeyDialogFragment(String profileName, GeneratePrivateKeyNoticeDialogListener listener) {
        super();
        this.profileName = profileName;
        this.listener = listener;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface GeneratePrivateKeyNoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String profileName);
        public void onDialogNegativeClick(DialogFragment dialog, String profileName);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm_generate_private_key_profile)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        listener.onDialogPositiveClick(GeneratePrivateKeyDialogFragment.this, profileName);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        listener.onDialogNegativeClick(GeneratePrivateKeyDialogFragment.this, profileName);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
