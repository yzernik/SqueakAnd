package io.github.yzernik.squeakand.ui.electrum;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;

public class ElectrumFragment extends Fragment {

    private TextView mShowServerHost;
    private TextView mShowServerPort;
    private Button mConnectElectrumServerButton;
    private TextView mElectrumConnectionStatus;
    private TextView mShowLatestBlockHeight;
    private Button mSelectElectrumServerButton;
    private TextInputLayout mEnterServerHostname;
    private TextInputLayout mEnterServerPort;


    private ElectrumModel electrumModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_electrum, container, false);

        mShowServerHost = root.findViewById(R.id.enter_electrum_host);
        mShowServerPort = root.findViewById(R.id.enter_electrum_port);
        mConnectElectrumServerButton = root.findViewById(R.id.connect_electrum_server_button);
        mElectrumConnectionStatus = root.findViewById(R.id.connection_status_text);
        mSelectElectrumServerButton = root.findViewById(R.id.select_public_electrum_server_button);
        mEnterServerHostname = root.findViewById(R.id.electrum_host_input);
        mEnterServerPort = root.findViewById(R.id.electrum_port_input);
        mShowLatestBlockHeight = root.findViewById(R.id.latest_block_height_text);

        electrumModel = new ViewModelProvider(getActivity()).get(ElectrumModel.class);

        electrumModel.getElectrumServerAddress().observe(getViewLifecycleOwner(), new Observer<ElectrumServerAddress>() {
            @Override
            public void onChanged(@Nullable final ElectrumServerAddress electrumServerAddress) {
                // Update edit text fields with current address.
                Log.i(getTag(),"Observed new electrum server address: " + electrumServerAddress);
                if (electrumServerAddress != null) {
                    mShowServerHost.setText(electrumServerAddress.getHost());
                    mShowServerPort.setText(Integer.toString(electrumServerAddress.getPort()));
                }
            }
        });

        electrumModel.getConnectionStatus().observe(getViewLifecycleOwner(), new Observer<ElectrumBlockchainRepository.ConnectionStatus>() {
            @Override
            public void onChanged(@Nullable final ElectrumBlockchainRepository.ConnectionStatus connectionStatus) {
                // Update edit text fields with current connection status.
                Log.i(getTag(),"Observed new electrum server connection status: " + connectionStatus);
                String connectionStatusString = connectionStatus.toString();
                mElectrumConnectionStatus.setText(connectionStatusString);
            }
        });

        electrumModel.getLatestBlock().observe(getViewLifecycleOwner(), new Observer<BlockInfo>() {
            @Override
            public void onChanged(@Nullable final BlockInfo blockInfo) {
                if (blockInfo != null) {
                    Log.i(getTag(),"Observed new block: " + blockInfo);
                    mShowLatestBlockHeight.setText(Integer.toString(blockInfo.getHeight()));
                }
            }
        });

        // Update the electrum server being used to download block headers
        mConnectElectrumServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getTag(), "Input hostname is: " + mEnterServerHostname.getEditText().getText());
                String host = mEnterServerHostname.getEditText().getText().toString();
                Log.i(getTag(), "Input port is: " + mEnterServerPort.getEditText().getText());
                int port = Integer.parseInt(mEnterServerPort.getEditText().getText().toString());
                ElectrumServerAddress serverAddress = new ElectrumServerAddress(host, port);
                Log.i(getTag(), "Updating electrum server with new address: " + serverAddress);
                electrumModel.setElectrumServerAddress(serverAddress);
            }
        });

        electrumModel.getServers().observe(getViewLifecycleOwner(), new Observer<List<ElectrumServerAddress>>() {
            @Override
            public void onChanged(@Nullable final List<ElectrumServerAddress> servers) {
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
    private void showSelectElectrumServerDialog(List<ElectrumServerAddress> addresses) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an electrum server");
        // add a list
        ArrayList<String> displayValues=new ArrayList<>();
        for (ElectrumServerAddress address : addresses) {
            displayValues.add(address.toString());
        }
        String[] displayValuesArr = displayValues.toArray(new String[displayValues.size()]);
        builder.setItems(displayValuesArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ElectrumServerAddress selectedAddress = addresses.get(which);
                String hostname = selectedAddress.getHost();
                String port = Integer.toString(selectedAddress.getPort());
                mEnterServerHostname.getEditText().setText(hostname);
                mEnterServerPort.getEditText().setText(port);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
