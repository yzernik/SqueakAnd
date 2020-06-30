package io.github.yzernik.squeakand.ui.money;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
    private Button mReceiveBitcoinsButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_money_balance, container, false);

        mSyncedToChainText = root.findViewById(R.id.synced_to_chain_text);
        mSyncedToGraphText = root.findViewById(R.id.synced_to_graph_text);
        mConfirmedBalance = root.findViewById(R.id.confirmed_balance_text);
        mUnconfirmedBalance = root.findViewById(R.id.unconfirmed_balance_text);
        mTotalBalance = root.findViewById(R.id.total_balance_text);
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
        moneyViewModel.getInfo().observe(getViewLifecycleOwner(), new Observer<DataResult<Rpc.GetInfoResponse>>() {
            @Override
            public void onChanged(DataResult<Rpc.GetInfoResponse> response) {
                if (!response.isSuccess()) {
                    return;
                }
                mSyncedToChainText.setText(Boolean.toString(response.getResponse().getSyncedToChain()));
                mSyncedToGraphText.setText(Boolean.toString(response.getResponse().getSyncedToGraph()));
            }
        });

        // Get wallet balance
        moneyViewModel.walletBalance().observe(getViewLifecycleOwner(), new Observer<DataResult<Rpc.WalletBalanceResponse>>() {
            @Override
            public void onChanged(DataResult<Rpc.WalletBalanceResponse> response) {
                if (!response.isSuccess()) {
                    return;
                }
                Rpc.WalletBalanceResponse walletBalanceResponse = response.getResponse();
                mUnconfirmedBalance.setText(Long.toString(walletBalanceResponse.getUnconfirmedBalance()));
                mConfirmedBalance.setText(Long.toString(walletBalanceResponse.getConfirmedBalance()));
                mTotalBalance.setText(Long.toString(walletBalanceResponse.getTotalBalance()));
            }
        });


        /*
        // Get channels
        moneyViewModel.listChannels().observe(getViewLifecycleOwner(), new Observer<Rpc.ListChannelsResponse>() {
            @Override
            public void onChanged(Rpc.ListChannelsResponse response) {
                if (response == null) {
                    return;
                }
                // TODO: create a recyclerview with the channels.
                Log.i(getTag(), "Got channels: " + response.getChannelsList());
            }
        });

        // Get peers
        moneyViewModel.listPeers().observe(getViewLifecycleOwner(), new Observer<Rpc.ListPeersResponse>() {
            @Override
            public void onChanged(Rpc.ListPeersResponse response) {
                if (response == null) {
                    return;
                }
                // TODO: create a recyclerview with the channels.
                Log.i(getTag(), "Got number of peers: " + response.getPeersList());
                for (Rpc.Peer peer: response.getPeersList()) {
                    Log.i(getTag(), "Got peer: " + peer);
                }
            }
        });*/

    }


    /*
    private void showReceiveAddressAlertDialog(LayoutInflater inflater, String receiveAddress) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Receive bitcoins");
        String msg = String.format("Receive address: %s", receiveAddress);
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }*/


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
