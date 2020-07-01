package io.github.yzernik.squeakand.ui.money;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;

public class NoWalletInitializedFragment extends Fragment {

    private Fragment targetFragment;

    private NoWalletInitializedModel noWalletInitializedModel;

    private Button mCreateWalletButton;
    private View mLockedWalletView;
    private View mUnlockedWalletView;

    public NoWalletInitializedFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_no_wallet_initialized, container, false);

        mCreateWalletButton = root.findViewById(R.id.no_wallet_initialized_create_wallet_button);
        mLockedWalletView = root.findViewById(R.id.no_wallet_initialized_locked_view);
        mUnlockedWalletView = root.findViewById(R.id.no_wallet_fragment_frame);

        noWalletInitializedModel = new ViewModelProvider(this).get(NoWalletInitializedModel.class);

        noWalletInitializedModel.getLiveHasWallet().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean hasWallet) {
                // Show the waiting fragment when wallet exists.
                Log.i(getTag(),"Got new value of hasWallet: " + hasWallet);
                if (hasWallet) {
                    showWaitingForWalletUnlocked();
                } else {
                    hideWaitingForWalletUnlocked();
                }
            }
        });

        // Set up button click to create new wallet.
        mCreateWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noWalletInitializedModel.createWallet();
                showWaitingForWalletUnlocked();
            }
        });


        return root;
    }


    private void showWaitingForWalletUnlocked() {
        // Hide the locked wallet view.
        mLockedWalletView.setVisibility(View.GONE);

        // Show the unlocked wallet view and start the waiting fragment.
        mUnlockedWalletView.setVisibility(View.VISIBLE);
        Fragment newFragment = new WaitingForWalletFragment(targetFragment);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(mUnlockedWalletView.getId(), newFragment);
        transaction.commit();
    }

    private void hideWaitingForWalletUnlocked() {
        // Show the locked wallet view.
        mLockedWalletView.setVisibility(View.VISIBLE);

        // Hide the unlocked wallet view.
        mUnlockedWalletView.setVisibility(View.GONE);
    }
}
