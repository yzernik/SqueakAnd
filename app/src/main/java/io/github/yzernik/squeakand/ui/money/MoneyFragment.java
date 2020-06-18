package io.github.yzernik.squeakand.ui.money;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class MoneyFragment extends Fragment {

    private MoneyViewModel moneyViewModel;

    private TextView mLightningNodePubKeyText;
    private TextView mConfirmedBalance;
    private TextView mTotalBalance;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_money, container, false);

        mLightningNodePubKeyText = root.findViewById(R.id.lightning_node_pubkey_text);
        mConfirmedBalance = root.findViewById(R.id.confirmed_balance_text);
        mTotalBalance = root.findViewById(R.id.total_balance_text);

        // Get a new or existing ViewModel from the ViewModelProvider.
        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);

        updateGetInfo();

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

                Log.i(getTag(), "Got block height from getInfo response: " + response.getBlockHeight());
                mLightningNodePubKeyText.setText(response.getIdentityPubkey());
            }
        });

        // Get wallet balance
        moneyViewModel.walletBalance().observe(getViewLifecycleOwner(), new Observer<Rpc.WalletBalanceResponse>() {
            @Override
            public void onChanged(Rpc.WalletBalanceResponse response) {
                if (response == null) {
                    return;
                }

                Log.i(getTag(), "Got confirmed balance from wallet balance response: " + response.getConfirmedBalance());
                Log.i(getTag(), "Got total balance from wallet balance response: " + response.getTotalBalance());
                mConfirmedBalance.setText(Long.toString(response.getConfirmedBalance()));
                mTotalBalance.setText(Long.toString(response.getTotalBalance()));
            }
        });

    }

}
