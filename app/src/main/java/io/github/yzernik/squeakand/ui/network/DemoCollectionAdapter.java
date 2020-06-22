package io.github.yzernik.squeakand.ui.network;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
            default:
                return null;
        }
    }

    private Fragment getDemoObjectFragment() {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = new DemoObjectFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}