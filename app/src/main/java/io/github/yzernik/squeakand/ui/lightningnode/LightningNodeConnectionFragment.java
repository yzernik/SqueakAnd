package io.github.yzernik.squeakand.ui.lightningnode;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.List;

import io.github.yzernik.squeakand.MoneyActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.DataResult;
import lnrpc.Rpc;

public class LightningNodeConnectionFragment extends Fragment {

    private TextView mLightningNodePubkeyText;
    private TextView mLightningNodeConnectionStatusText;
    private Button mLightningNodeConnectButton;
    private TextView mLightningNodeChannelsText;
    private Button mLightningNodeOpenChannelButton;
    private TextView mLightningBalanceText;
    private Button mLightningNodeViewWalletButton;

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
        mLightningNodeChannelsText = root.findViewById(R.id.lightning_node_channels_text);
        mLightningNodeOpenChannelButton = root.findViewById(R.id.lightning_node_open_channel_button);
        mLightningBalanceText = root.findViewById(R.id.lightning_node_balance_text);
        mLightningNodeViewWalletButton = root.findViewById(R.id.lightning_node_view_wallet_button);

        // Start with buttons hidden.
        mLightningNodeConnectButton.setVisibility(View.GONE);
        mLightningNodeOpenChannelButton.setVisibility(View.GONE);
        mLightningNodeViewWalletButton.setVisibility(View.GONE);

        lightningNodeChannelsModel = ViewModelProviders.of(this,
                new LightningNodeConnectionModelFactory(getActivity().getApplication(), pubkey, host))
                .get(LightningNodeConnectionModel.class);

        mLightningNodePubkeyText.setText("Pubkey: " + pubkey);

        lightningNodeChannelsModel.liveIsPeerConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean isPeerConnected) {
                Log.i(getTag(), "Is peer connected: " + isPeerConnected);
                mLightningNodeConnectionStatusText.setText("Is peer connected: " + isPeerConnected);

                // Handle the connect peer button.
                if (lightningNodeChannelsModel.getHost() != null) {
                    mLightningNodeConnectButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LiveData<DataResult<Rpc.ConnectPeerResponse>> connectResult = lightningNodeChannelsModel.connectToPeer();
                            handleConnectPeerResult(connectResult);
                        }
                    });
                }

                // Don't show the connect button if already connected
                if (isPeerConnected) {
                    mLightningNodeConnectButton.setVisibility(View.GONE);
                } else {
                    mLightningNodeConnectButton.setVisibility(View.VISIBLE);
                }
            }
        });


        lightningNodeChannelsModel.listNodeChannels().observe(getViewLifecycleOwner(), new Observer<List<Rpc.Channel>>() {
            @Override
            public void onChanged(@Nullable final List<Rpc.Channel> channels) {
                int numChannels = channels.size();
                mLightningNodeChannelsText.setText("Number of channels to node:" + numChannels);

                // Only show open channel button if connected.
                mLightningNodeOpenChannelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LiveData<DataResult<Rpc.ChannelPoint>> openChanelResult = lightningNodeChannelsModel.openChannel(20000);
                        handleOpenChannelResult(openChanelResult);
                    }
                });
                mLightningNodeOpenChannelButton.setVisibility(View.VISIBLE);
            }
        });


        lightningNodeChannelsModel.liveConfirmedBalance().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(@Nullable final Long confirmedBalance) {
                mLightningBalanceText.setText("Confirmed wallet balance:" + confirmedBalance);

                // Set up the go to wallet button
                mLightningNodeViewWalletButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startWalletActivity();
                    }
                });
                mLightningNodeViewWalletButton.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    private void handleConnectPeerResult(LiveData<DataResult<Rpc.ConnectPeerResponse>> connectResult) {
        connectResult.observe(getViewLifecycleOwner(), new Observer<DataResult<Rpc.ConnectPeerResponse>>() {
            @Override
            public void onChanged(@Nullable final DataResult<Rpc.ConnectPeerResponse> result) {
                if (!result.isSuccess()) {
                    Log.e(getTag(), "Connect peer failed with error: " + result.getError());
                    showFailedConnectPeerAlert(result.getError());
                }
            }
        });
    }

    private void handleOpenChannelResult(LiveData<DataResult<Rpc.ChannelPoint>> openChannelResult) {
        openChannelResult.observe(getViewLifecycleOwner(), new Observer<DataResult<Rpc.ChannelPoint>>() {
            @Override
            public void onChanged(@Nullable final DataResult<Rpc.ChannelPoint> result) {
                if (result.isFailure()) {
                    Log.e(getTag(), "Open channel failed with error: " + result.getError());
                    showFailedOpenChannelAlert(result.getError());
                    return;
                }
                Rpc.ChannelPoint channelPoint = result.getResponse();
                showSuccessOpenChannelAlert(channelPoint);
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

    private void showSuccessOpenChannelAlert(Rpc.ChannelPoint channelPoint) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Opened channel");
        String pubkey = lightningNodeChannelsModel.getPubkey();
        alertDialog.setMessage(
                "New channel opened to peer: " + pubkey + "." +
                "\nChannel is now pending and will be available when the transaction is confirmed.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void startWalletActivity() {
        Log.i(getTag(), "Starting money activity...");
        startActivity(new Intent(getActivity(), MoneyActivity.class));
    }

}
