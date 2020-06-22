package io.github.yzernik.squeakand.ui.network;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.ServerListAdapter;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.ViewServerActivity;
import io.github.yzernik.squeakand.server.SqueakServerAddress;

public class DemoObjectFragment extends Fragment implements ServerListAdapter.ClickListener {

    private NetworkViewModel networkViewModel;

    private Button mAddServerButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(getTag(), "starting DemoObjectFragment...");


        View root = inflater.inflate(R.layout.fragment_network_squeak, container, false);

        final RecyclerView recyclerView = root.findViewById(R.id.serversRecyclerView);
        final ServerListAdapter adapter = new ServerListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        mAddServerButton = root.findViewById(R.id.add_server_button);
        Log.i(getTag(), "Got mAddServerButton: " + mAddServerButton);

        // Get a new or existing ViewModel from the ViewModelProvider.
        networkViewModel = new ViewModelProvider(this).get(NetworkViewModel.class);

        mAddServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Add server button clicked");
                Log.i(getTag(), "Add server button clicked");
                showAddServerAlertDialog(inflater);
            }
        });

        networkViewModel.getSqueakServers().observe(getViewLifecycleOwner(), new Observer<List<SqueakServer>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakServer> squeakServers) {
                Log.i(getTag(), "Got squeakServers from model.");

                // Update the cached copy of the servers in the adapter.
                adapter.setServers(squeakServers);
            }
        });


        return root;
    }


    private void showAddServerAlertDialog(LayoutInflater inflater) {
        final View view = inflater.inflate(R.layout.dialog_add_server, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Add server");

        final EditText serverHostText = (EditText) view.findViewById(R.id.dialog_add_server_host_input);
        final EditText serverPortText = (EditText) view.findViewById(R.id.dialog_add_server_port_input);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String hostString = serverHostText.getText().toString();
                        if (hostString == null || hostString.isEmpty()) {
                            return;
                        }

                        String portString = serverPortText.getText().toString();
                        if (portString == null || portString.isEmpty()) {
                            return;
                        }
                        int port = -1;
                        try {
                            port = Integer.parseInt(portString);
                        } catch (NumberFormatException e) {
                            return;
                        }

                        SqueakServerAddress serverAddress = new SqueakServerAddress(hostString, port);
                        SqueakServer squeakServer = new SqueakServer("", serverAddress);

                        Log.i(getTag(), "New server class: " + squeakServer);
                        networkViewModel.insert(squeakServer);
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


    @Override
    public void handleItemClick(int id) {
        // TODO: go to server activity
        Log.i(getTag(), "Clicked on server id: " + id);
        startActivity(new Intent(getActivity(), ViewServerActivity.class).putExtra("server_id", id));
    }

}