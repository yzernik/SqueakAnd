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

    private NoWalletModel noWalletModel;

/*
    private TextView mHasWalletText;
    private TextView mIsWalletUnlockedText;
    private TextView mIsButtonClickedText;
    private Button mCreateWalletButton;
*/
    private View mLockedWalletView;
    private View mUnlockedWalletView;

    public WaitingForWalletFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_waiting_for_wallet, container, false);

/*        mHasWalletText = root.findViewById(R.id.create_wallet_has_wallet_text);
        mIsWalletUnlockedText = root.findViewById(R.id.create_wallet_is_wallet_unlocked_text);
        mIsButtonClickedText = root.findViewById(R.id.create_wallet_is_button_clicked_text);
        mCreateWalletButton = root.findViewById(R.id.create_wallet_button);*/
        mLockedWalletView = root.findViewById(R.id.waiting_for_wallet_locked_view);
        mUnlockedWalletView = root.findViewById(R.id.no_wallet_unlocked_fragment_frame);

        noWalletModel = new ViewModelProvider(this).get(NoWalletModel.class);

        noWalletModel.getLiveHasWallet().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean hasWallet) {
                // mHasWalletText.setText(hasWallet.toString());
            }
        });

        noWalletModel.getLiveIsWalletUnlocked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isWalletUnlocked) {
                // mIsWalletUnlockedText.setText(isWalletUnlocked.toString());

                // Show the hidden fragment when wallet is unlocked.
                Log.i(getTag(),"Got new value of isWalletUnlocked: " + isWalletUnlocked);
                if (isWalletUnlocked) {
                    showWalletUnlocked();
                }
            }
        });

        noWalletModel.getLiveIsButtonClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isButtonClicked) {
                // mIsButtonClickedText.setText(isButtonClicked.toString());
                // Hide the linearlayout, and only show the hidden fragment.

                Log.i(getTag(),"Got new value of isButtonClicked: " + isButtonClicked);
                if (isButtonClicked) {
                    showWalletUnlocked();
                }
            }
        });

/*        mCreateWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noWalletModel.buttonClicked();
            }
        });*/

        // Wait for wallet to be unlocked on fragment start.
        noWalletModel.waitForWalletUnlocked();

        return root;
    }

    private void showWalletUnlocked() {
        // Hide the locked wallet view.
        mLockedWalletView.setVisibility(View.GONE);

        // Show the unlocked wallet view and start the target fragment.
        mUnlockedWalletView.setVisibility(View.VISIBLE);
        Fragment newFragment = targetFragment;
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.no_wallet_unlocked_fragment_frame, newFragment);
        transaction.commit();
    }


}
