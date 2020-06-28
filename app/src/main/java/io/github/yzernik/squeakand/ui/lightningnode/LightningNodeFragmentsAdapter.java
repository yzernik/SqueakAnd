package io.github.yzernik.squeakand.ui.lightningnode;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LightningNodeFragmentsAdapter extends FragmentStateAdapter {

    private final String pubkey;
    @Nullable
    private final String host;

    public LightningNodeFragmentsAdapter(Fragment fragment, String pubkey, String host) {
        super(fragment);
        this.pubkey = pubkey;
        this.host = host;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return getConnectionFragment(pubkey, host);
            case 1:
                return getChannelsFragment(pubkey);
            default:
                return null;
        }
    }

    private Fragment getConnectionFragment(String pubkey, String host) {
        Fragment fragment = new LightningNodeConnectionFragment();
        Bundle args = new Bundle();
        args.putString("pubkey", pubkey);
        args.putString("host", host);
        fragment.setArguments(args);
        return fragment;
    }

    private Fragment getChannelsFragment(String pubkey) {
        Fragment fragment = new LightningNodeChannelsFragment();
        Bundle args = new Bundle();
        args.putString("pubkey", pubkey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}