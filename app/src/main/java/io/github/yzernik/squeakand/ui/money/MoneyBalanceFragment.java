package io.github.yzernik.squeakand.ui.money;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.DataResult;
import lnrpc.Rpc;

public class MoneyBalanceFragment extends Fragment {

    private MoneyViewModel moneyViewModel;

    private TextView mSyncedToChainText;
    private TextView mSyncedToGraphText;
    private TextView mConfirmedBalance;
    private TextView mUnconfirmedBalance;
    private TextView mTotalBalance;
    private TextView mOpenChannelsCountText;
    private TextView mPendingOpenChannelsCountText;
    private TextView mPendingCloseChannelsCountText;
    private TextView mPendingForceCloseChannelsCountText;
    private Button mReceiveBitcoinsButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_money_balance, container, false);

        mSyncedToChainText = root.findViewById(R.id.synced_to_chain_text);
        mSyncedToGraphText = root.findViewById(R.id.synced_to_graph_text);
        mConfirmedBalance = root.findViewById(R.id.confirmed_balance_text);
        mUnconfirmedBalance = root.findViewById(R.id.unconfirmed_balance_text);
        mTotalBalance = root.findViewById(R.id.total_balance_text);
        mOpenChannelsCountText = root.findViewById(R.id.money_open_channels_count_text);
        mPendingOpenChannelsCountText = root.findViewById(R.id.money_pending_open_channels_count_text);
        mPendingCloseChannelsCountText = root.findViewById(R.id.money_pending_close_channels_count_text);
        mPendingForceCloseChannelsCountText = root.findViewById(R.id.money_pending_force_close_channels_count_text);
        mReceiveBitcoinsButton = root.findViewById(R.id.receive_bitcoins_button);

        // Get a new or existing ViewModel from the ViewModelProvider.
        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);

        updateGetInfo();

        mReceiveBitcoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // New address
                moneyViewModel.newAddress().observe(getViewLifecycleOwner(), new Observer<DataResult<Rpc.NewAddressResponse>>() {
                    @Override
                    public void onChanged(DataResult<Rpc.NewAddressResponse> response) {
                        if (!response.isSuccess()) {
                            return;
                        }
                        String address = response.getResponse().getAddress();
                        showReceiveAddressAlertDialog(inflater, address);
                    }
                });
            }
        });

        return root;
    }


    private void updateGetInfo () {

        // Get info
        moneyViewModel.getInfo().observe(getViewLifecycleOwner(), new Observer<Rpc.GetInfoResponse>() {
            @Override
            public void onChanged(Rpc.GetInfoResponse response) {
                mSyncedToChainText.setText(Boolean.toString(response.getSyncedToChain()));
                mSyncedToGraphText.setText(Boolean.toString(response.getSyncedToGraph()));
            }
        });

        // Get wallet balance
        moneyViewModel.walletBalance().observe(getViewLifecycleOwner(), new Observer<Rpc.WalletBalanceResponse>() {
            @Override
            public void onChanged(Rpc.WalletBalanceResponse response) {
                mUnconfirmedBalance.setText(Long.toString(response.getUnconfirmedBalance()));
                mConfirmedBalance.setText(Long.toString(response.getConfirmedBalance()));
                mTotalBalance.setText(Long.toString(response.getTotalBalance()) + " satoshis");
            }
        });

        // Get open channels
        moneyViewModel.listChannels().observe(getViewLifecycleOwner(), new Observer<Rpc.ListChannelsResponse>() {
            @Override
            public void onChanged(Rpc.ListChannelsResponse response) {
                List<Rpc.Channel> channels = response.getChannelsList();
                int channelsCount = channels.size();
                String openChannelsCountString = Integer.toString(channelsCount);
                mOpenChannelsCountText.setText(openChannelsCountString);
            }
        });

        // Get pending channels
        moneyViewModel.pendingChannels().observe(getViewLifecycleOwner(), new Observer<Rpc.PendingChannelsResponse>() {
            @Override
            public void onChanged(Rpc.PendingChannelsResponse response) {
                Log.i(getTag(), "Got PendingChannelsResponse:" + response);
                String pendingOpenChannelsCountString = Integer.toString(response.getPendingOpenChannelsCount());
                String pendingCloseChannelsCountString = Integer.toString(response.getWaitingCloseChannelsCount());
                String pendingForceCloseChannelsCountString = Integer.toString(response.getPendingForceClosingChannelsCount());
                mPendingOpenChannelsCountText.setText(pendingOpenChannelsCountString);
                mPendingCloseChannelsCountText.setText(pendingCloseChannelsCountString);
                mPendingForceCloseChannelsCountText.setText(pendingForceCloseChannelsCountString);
            }
        });
    }

    private void showReceiveAddressAlertDialog(LayoutInflater inflater, String receiveAddress) {
        final View view = inflater.inflate(R.layout.dialog_receive_bitcoins, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Receive bitcoins");

        final TextView receiveBitoinsAddressText = (TextView) view.findViewById(R.id.receive_bitcoins_address);
        receiveBitoinsAddressText.setText(receiveAddress);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setView(view);
        alertDialog.show();
    }

}
