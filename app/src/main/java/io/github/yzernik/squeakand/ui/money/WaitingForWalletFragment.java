package io.github.yzernik.squeakand.ui.money;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;

public class WaitingForWalletFragment extends Fragment {

    private Fragment targetFragment;

    private WaitingForWalletModel waitingForWalletModel;

    private View mLockedWalletView;
    private View mUnlockedWalletView;

    public WaitingForWalletFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_waiting_for_wallet, container, false);

        mLockedWalletView = root.findViewById(R.id.waiting_for_wallet_locked_view);
        mUnlockedWalletView = root.findViewById(R.id.waiting_for_wallet_unlocked_fragment_frame);

        waitingForWalletModel = new ViewModelProvider(this).get(WaitingForWalletModel.class);

        waitingForWalletModel.getLiveIsWalletUnlocked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isWalletUnlocked) {
                // Show the hidden fragment when wallet is unlocked.
                Log.i(getTag(),"Got new value of isWalletUnlocked: " + isWalletUnlocked);
                if (isWalletUnlocked) {
                    showWalletUnlocked();
                }
            }
        });

        // Wait for wallet to be unlocked on fragment start.
        waitingForWalletModel.waitForWalletUnlocked();

        return root;
    }

    private void showWalletUnlocked() {
        // Hide the locked wallet view.
        mLockedWalletView.setVisibility(View.GONE);

        // Show the unlocked wallet view and start the target fragment.
        mUnlockedWalletView.setVisibility(View.VISIBLE);
        Fragment newFragment = targetFragment;
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.waiting_for_wallet_unlocked_fragment_frame, newFragment);
        transaction.commit();
    }


}
