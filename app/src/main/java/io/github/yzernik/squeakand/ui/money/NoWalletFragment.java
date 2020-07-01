package io.github.yzernik.squeakand.ui.money;

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

public class NoWalletFragment extends Fragment {

    private Fragment targetFragment;

    private NoWalletModel noWalletModel;

    private TextView mHasWalletText;
    private TextView mIsWalletUnlockedText;
    private Button mCreateWalletButton;

    public NoWalletFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_no_wallet, container, false);

        mHasWalletText = root.findViewById(R.id.create_wallet_has_wallet_text);
        mIsWalletUnlockedText = root.findViewById(R.id.create_wallet_is_wallet_unlocked_text);
        mCreateWalletButton = root.findViewById(R.id.create_wallet_button);

        noWalletModel = new ViewModelProvider(this).get(NoWalletModel.class);

        noWalletModel.getLiveHasWallet().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean hasWallet) {
                mHasWalletText.setText(hasWallet.toString());
            }
        });

        noWalletModel.getLiveIsWalletUnlocked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isWalletUnlocked) {
                mIsWalletUnlockedText.setText(isWalletUnlocked.toString());
            }
        });

        return root;
    }



}
