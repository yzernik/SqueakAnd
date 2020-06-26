package io.github.yzernik.squeakand.ui.money;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.yzernik.squeakand.ui.channels.ChannelsFragment;

public class MoneyFragmentsAdapter extends FragmentStateAdapter {

    public MoneyFragmentsAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return getBalanceFragment();
            case 1:
                return getChannelsFragment();
            default:
                return null;
        }
    }

    private Fragment getBalanceFragment() {
        Fragment fragment = new MoneyBalanceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Fragment getChannelsFragment() {
        Fragment fragment = new ChannelsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}