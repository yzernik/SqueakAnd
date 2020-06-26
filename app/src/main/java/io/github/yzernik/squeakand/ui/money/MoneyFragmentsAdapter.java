package io.github.yzernik.squeakand.ui.money;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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


    @Override
    public int getItemCount() {
        return 1;
    }
}