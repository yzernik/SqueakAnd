package io.github.yzernik.squeakand.ui.network;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.yzernik.squeakand.ui.electrum.ElectrumFragment;

public class DemoCollectionAdapter extends FragmentStateAdapter {
    public DemoCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return getDemoObjectFragment();
            case 1:
                return getElectrumFragment();
            default:
                return null;
        }
    }

    private Fragment getDemoObjectFragment() {
        Fragment fragment = new DemoObjectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Fragment getElectrumFragment() {
        Fragment fragment = new ElectrumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}