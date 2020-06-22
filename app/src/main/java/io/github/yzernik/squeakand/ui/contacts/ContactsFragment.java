package io.github.yzernik.squeakand.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.github.yzernik.squeakand.R;

public class ContactsFragment extends Fragment {
    ContactsFragmentsAdapter contactsFragmentsAdapter;
    ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        contactsFragmentsAdapter = new ContactsFragmentsAdapter(this);
        viewPager = view.findViewById(R.id.contacts_pager);
        viewPager.setAdapter(contactsFragmentsAdapter);

        TabLayout tabLayout = view.findViewById(R.id.contacts_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getTabName(position))
        ).attach();
    }

    private String getTabName(int position) {
        switch(position) {
            case 0:
                return "Signing Profiles";
            default:
                return null;
        }
    }
}
