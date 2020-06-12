package io.github.yzernik.squeakand.ui.viewserver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakServer;

public class ViewServerFragment extends Fragment {

    private TextView mServerNameText;
    private TextView mServerAddressText;
    private Switch mSyncingSwitch;
    private Button mRenameServerButton;
    private Button mDeleteServerButton;

    private ViewServerModel viewServerModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_server, container, false);

        // TODO: Get the server id from the bundle.
        int serverId = this.getArguments().getInt("server_id", -1);
        Log.i(getTag(), "Running frament with server id: " + serverId);

        mServerNameText = root.findViewById(R.id.view_server_show_name);
        mServerAddressText = root.findViewById(R.id.view_server_show_address);
        mSyncingSwitch = root.findViewById(R.id.view_server_syncing_switch);
        mRenameServerButton = root.findViewById(R.id.view_server_rename_button);
        mDeleteServerButton = root.findViewById(R.id.view_server_delete_button);

        // Get a new or existing ViewModel from the ViewModelProvider.
        viewServerModel = new ViewModelProvider(this).get(ViewServerModel.class);

        viewServerModel.getSqueakServer(serverId).observe(getViewLifecycleOwner(), new Observer<SqueakServer>() {
            @Override
            public void onChanged(@Nullable SqueakServer squeakServer) {
                if (squeakServer == null) {
                    return;
                }

                mServerNameText.setText(squeakServer.getName());
                mServerAddressText.setText(squeakServer.getAddress().toString());

                // TODO: Add the syncing field to the database and use that here.
                mSyncingSwitch.setChecked(true);
                /*
                mSyncingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        squeakServer.syncing = isChecked;
                        Log.i(getTag(), "New server class: " + squeakServer);
                        viewServerModel.updateServer(squeakServer);
                    }
                });*/

                // Setup the rename button.
                mRenameServerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRenameAlertDialog(inflater, squeakServer);
                    }
                });

                // Setup the delete button.
                mDeleteServerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteAlertDialog(inflater, squeakServer);
                    }
                });
            }
        });

        return root;
    }

    private void showRenameAlertDialog(LayoutInflater inflater, SqueakServer squeakServer) {
        // TODO
    }

    private void showDeleteAlertDialog(LayoutInflater inflater, SqueakServer squeakServer) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Delete server");
        alertDialog.setMessage("Are you sure you want to delete this server?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(getTag(), "Deleting server: " + squeakServer);
                        viewServerModel.deleteServer(squeakServer);
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


}
