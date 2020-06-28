package io.github.yzernik.squeakand.ui.lightningnode;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.lnd.LndResult;
import lnrpc.Rpc;

public class LightningNodeConnectionFragment extends Fragment {

    private TextView mLightningNodePubkeyText;
    private TextView mLightningNodeConnectionStatusText;
    private Button mLightningNodeConnectButton;
    private Button mLightningNodeOpenChannelButton;

    private LightningNodeConnectionModel lightningNodeChannelsModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lightning_node_connection, container, false);

        String pubkey = null;
        String host = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            pubkey = this.getArguments().getString("pubkey");
            host = this.getArguments().getString("host");
        }

        mLightningNodePubkeyText = root.findViewById(R.id.lightning_node_pubkey);
        mLightningNodeConnectionStatusText = root.findViewById(R.id.lightning_node_connection_status_text);
        mLightningNodeConnectButton = root.findViewById(R.id.lightning_node_connect_peer_button);
        mLightningNodeOpenChannelButton = root.findViewById(R.id.lightning_node_open_channel_button);

        lightningNodeChannelsModel = ViewModelProviders.of(this,
                new LightningNodeConnectionModelFactory(getActivity().getApplication(), pubkey, host))
                .get(LightningNodeConnectionModel.class);

        mLightningNodePubkeyText.setText("Pubkey: " + pubkey);

        lightningNodeChannelsModel.liveIsPeerConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean isPeerConnected) {
                mLightningNodeConnectionStatusText.setText("Is peer connected: " + isPeerConnected);
            }
        });

        // Only show connect button if the host variable is set.
        if (lightningNodeChannelsModel.getHost() != null) {
            mLightningNodeConnectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LiveData<LndResult<Rpc.ConnectPeerResponse>> connectResult = lightningNodeChannelsModel.connectToPeer();
                    handleConnectPeerResult(connectResult);
                }
            });
        }


        // Only show open channel button if connected.
        mLightningNodeOpenChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveData<LndResult<Rpc.ChannelPoint>> openChanelResult = lightningNodeChannelsModel.openChannel(20000);
                handleOpenChannelResult(openChanelResult);
            }
        });

        return root;
    }

    private void handleConnectPeerResult(LiveData<LndResult<Rpc.ConnectPeerResponse>> connectResult) {
        connectResult.observe(getViewLifecycleOwner(), new Observer<LndResult<Rpc.ConnectPeerResponse>>() {
            @Override
            public void onChanged(@Nullable final LndResult<Rpc.ConnectPeerResponse> result) {
                if (!result.isSuccess()) {
                    Log.e(getTag(), "Connect peer failed with error: " + result.getError());
                    showFailedConnectPeerAlert(result.getError());
                }
            }
        });
    }

    private void handleOpenChannelResult(LiveData<LndResult<Rpc.ChannelPoint>> openChannelResult) {
        openChannelResult.observe(getViewLifecycleOwner(), new Observer<LndResult<Rpc.ChannelPoint>>() {
            @Override
            public void onChanged(@Nullable final LndResult<Rpc.ChannelPoint> result) {
                if (!result.isSuccess()) {
                    Log.e(getTag(), "Open channel failed with error: " + result.getError());
                    showFailedOpenChannelAlert(result.getError());
                }
            }
        });
    }

    private void showFailedConnectPeerAlert(Throwable e) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Connect peer failed");
        alertDialog.setMessage("Failed with error: " + e);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showFailedOpenChannelAlert(Throwable e) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Open channel failed");
        alertDialog.setMessage("Failed with error: " + e);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
