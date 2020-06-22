package io.github.yzernik.squeakand.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.yzernik.squeakand.ui.manageprofiles.ManageProfilesFragment;


public class ContactsFragmentsAdapter extends FragmentStateAdapter {
    public ContactsFragmentsAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return getContactsFragment();
            case 1:
                return getManageProfilesFragment();
            default:
                return null;
        }
    }

    private Fragment getContactsFragment() {
        Fragment fragment = new ContactsContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Fragment getManageProfilesFragment() {
        Fragment fragment = new ManageProfilesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}