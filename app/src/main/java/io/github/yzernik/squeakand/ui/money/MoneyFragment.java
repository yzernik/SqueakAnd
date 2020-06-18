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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_money, container, false);

        // mCreateWalletButton = root.findViewById(R.id.create_wallet_button);
        mLightningNodePubKeyText = root.findViewById(R.id.lightning_node_pubkey_text);

        // Get a new or existing ViewModel from the ViewModelProvider.
        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);

        updateGetInfo();

        return root;
    }


    private void updateGetInfo () {
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
    }

}
