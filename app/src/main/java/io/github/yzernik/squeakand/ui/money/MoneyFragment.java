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
import io.github.yzernik.squeakand.SqueakProfile;
import lnrpc.Rpc;

import static org.bitcoinj.core.Utils.HEX;

public class MoneyFragment extends Fragment {

    private MoneyViewModel moneyViewModel;

    private TextView mLightningNodePubKeyText;
    private TextView mSyncedToChainText;
    private TextView mSyncedToGraphText;
    private TextView mConfirmedBalance;
    private TextView mTotalBalance;
    private Button mReceiveBitcoinsButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_money, container, false);

        mLightningNodePubKeyText = root.findViewById(R.id.lightning_node_pubkey_text);
        mSyncedToChainText = root.findViewById(R.id.synced_to_chain_text);
        mSyncedToGraphText = root.findViewById(R.id.synced_to_graph_text);
        mConfirmedBalance = root.findViewById(R.id.confirmed_balance_text);
        mTotalBalance = root.findViewById(R.id.total_balance_text);
        mReceiveBitcoinsButton = root.findViewById(R.id.receive_bitcoins_button);

        // Get a new or existing ViewModel from the ViewModelProvider.
        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);

        updateGetInfo();

        mReceiveBitcoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // New address
                moneyViewModel.newAddress().observe(getViewLifecycleOwner(), new Observer<Rpc.NewAddressResponse>() {
                    @Override
                    public void onChanged(Rpc.NewAddressResponse response) {
                        if (response == null) {
                            return;
                        }
                        String address = response.getAddress();
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
                if (response == null) {
                    return;
                }
                mLightningNodePubKeyText.setText(response.getIdentityPubkey());
                mSyncedToChainText.setText(Boolean.toString(response.getSyncedToChain()));
                mSyncedToGraphText.setText(Boolean.toString(response.getSyncedToGraph()));
            }
        });

        // Get wallet balance
        moneyViewModel.walletBalance().observe(getViewLifecycleOwner(), new Observer<Rpc.WalletBalanceResponse>() {
            @Override
            public void onChanged(Rpc.WalletBalanceResponse response) {
                if (response == null) {
                    return;
                }
                mConfirmedBalance.setText(Long.toString(response.getConfirmedBalance()));
                mTotalBalance.setText(Long.toString(response.getTotalBalance()));
            }
        });

        // Get channels
        moneyViewModel.listChannels().observe(getViewLifecycleOwner(), new Observer<Rpc.ListChannelsResponse>() {
            @Override
            public void onChanged(Rpc.ListChannelsResponse response) {
                if (response == null) {
                    return;
                }
                // TODO: create a recyclerview with the channels.
            }
        });

    }

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
    }

}
