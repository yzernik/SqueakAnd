package io.github.yzernik.squeakand.ui.money;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class MoneyFragment extends Fragment {

    private MoneyViewModel moneyViewModel;

    private Button mCreateWalletButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_money, container, false);

        mCreateWalletButton = root.findViewById(R.id.create_wallet_button);

        // Get a new or existing ViewModel from the ViewModelProvider.
        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);

        mCreateWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getTag(), "Create wallet button clicked");
                moneyViewModel.initWallet();
            }
        });

        moneyViewModel.getInfo().observe(getViewLifecycleOwner(), new Observer<Rpc.GetInfoResponse>() {
            @Override
            public void onChanged(Rpc.GetInfoResponse response) {
                if (response == null) {
                    return;
                }

                Log.i(getTag(), "Got block height from getInfo response: " + response.getBlockHeight());
            }
        });

        return root;
    }

}
