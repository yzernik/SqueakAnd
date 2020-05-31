package io.github.yzernik.squeakand.ui.blockchain;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;

public class BlockchainFragment extends Fragment {

    private EditText mElectrumServerHost;
    private EditText mElectrumServerPort;
    private Button mUpdateElectrumServerButton;
    private TextView mElectrumConnectionStatus;
    private Button mSelectElectrumServerButton;


    private BlockchainModel blockchainModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blockchain, container, false);

        mElectrumServerHost = root.findViewById(R.id.enter_electrum_host);
        mElectrumServerPort = root.findViewById(R.id.enter_electrum_port);
        mUpdateElectrumServerButton = root.findViewById(R.id.update_electrum_server_button);
        mElectrumConnectionStatus = root.findViewById(R.id.connection_status_text);
        mSelectElectrumServerButton = root.findViewById(R.id.select_electrum_server_button);

        blockchainModel = new ViewModelProvider(getActivity()).get(BlockchainModel.class);

        blockchainModel.getElectrumServerAddress().observe(getViewLifecycleOwner(), new Observer<ElectrumServerAddress>() {
            @Override
            public void onChanged(@Nullable final ElectrumServerAddress electrumServerAddress) {
                // Update edit text fields with current address.
                Log.i(getTag(),"Observed new electrum server address: " + electrumServerAddress);
                if (electrumServerAddress != null) {
                    mElectrumServerHost.setText(electrumServerAddress.getHost());
                    mElectrumServerPort.setText(Integer.toString(electrumServerAddress.getPort()));
                }
            }
        });

        blockchainModel.getConnectionStatus().observe(getViewLifecycleOwner(), new Observer<ElectrumBlockchainRepository.ConnectionStatus>() {
            @Override
            public void onChanged(@Nullable final ElectrumBlockchainRepository.ConnectionStatus connectionStatus) {
                // Update edit text fields with current connection status.
                Log.i(getTag(),"Observed new electrum server connection status: " + connectionStatus);
                String connectionStatusString = connectionStatus.toString();
                mElectrumConnectionStatus.setText(connectionStatusString);
            }
        });

        // Update the electrum server being used to download block headers
        mUpdateElectrumServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = mElectrumServerHost.getText().toString();
                int port = Integer.valueOf(mElectrumServerPort.getText().toString());
                ElectrumServerAddress serverAddress = new ElectrumServerAddress(host, port);
                Log.i(getTag(), "Updating electrum server with new address: " + serverAddress);
                blockchainModel.setElectrumServerAddress(serverAddress);
            }
        });

        blockchainModel.getServers().observe(getViewLifecycleOwner(), new Observer<List<InetSocketAddress>>() {
            @Override
            public void onChanged(@Nullable final List<InetSocketAddress> servers) {
                mSelectElectrumServerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(getTag(),"Select electrum server button clicked");
                        showSelectElectrumServerDialog(servers);
                    }
                });
            }
        });


        return root;
    }


    /**
     * Show the alert dialog for selecting an electrum server.
     * @param addresses
     */
    private void showSelectElectrumServerDialog(List<InetSocketAddress> addresses) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an electrum server");
        // add a list
        ArrayList<String> displayValues=new ArrayList<>();
        for (InetSocketAddress address : addresses) {
            displayValues.add(address.toString());
        }
        String[] displayValuesArr = displayValues.toArray(new String[displayValues.size()]);
        builder.setItems(displayValuesArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InetSocketAddress selectedAddress = addresses.get(which);
                // TODO: do something with the selected server address.
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
