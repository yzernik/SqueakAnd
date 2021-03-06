package io.github.yzernik.squeakand.ui.money;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private MoneyMenuActions moneyMenuActions;

    private View mLockedWalletView;
    private View mUnlockedWalletView;

    public WaitingForWalletFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_waiting_for_wallet, container, false);

        // Enable the options menu
        setHasOptionsMenu(true);

        mLockedWalletView = root.findViewById(R.id.waiting_for_wallet_locked_view);
        mUnlockedWalletView = root.findViewById(R.id.waiting_for_wallet_unlocked_fragment_frame);

        waitingForWalletModel = new ViewModelProvider(this).get(WaitingForWalletModel.class);

        moneyMenuActions = new MoneyMenuActions(waitingForWalletModel);

        waitingForWalletModel.getLiveIsRpcReady().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isRpcReady) {
                // Show the hidden fragment when wallet is unlocked.
                Log.i(getTag(),"Got new value of isRpcReady: " + isRpcReady);
                if (isRpcReady) {
                    showWalletUnlocked();
                } else {
                    hideWalletUnlocked();
                }
            }
        });

        return root;
    }

    private void showWalletUnlocked() {
        // Hide the locked wallet view.
        mLockedWalletView.setVisibility(View.GONE);

        // Show the unlocked wallet view and start the target fragment.
        mUnlockedWalletView.setVisibility(View.VISIBLE);
        Fragment newFragment = targetFragment;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(mUnlockedWalletView.getId(), newFragment);
        transaction.commit();
    }

    private void hideWalletUnlocked() {
        // Hide the locked wallet view.
        mLockedWalletView.setVisibility(View.VISIBLE);

        // Show the unlocked wallet view.
        mUnlockedWalletView.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(getTag(), "Overriding onCreateOptionsMenu...");
        inflater.inflate(R.menu.wallet_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Set the refresh button action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.wallet_menu_backup:
                moneyMenuActions.showWalletBackupAlert(getContext());
                return true;
            case R.id.wallet_menu_delete:
                moneyMenuActions.showDeleteWalletAlertDialog(getContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
