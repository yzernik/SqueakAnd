package io.github.yzernik.squeakand.ui.money;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.github.yzernik.squeakand.R;

public class MoneyFragment extends Fragment {
    MoneyFragmentsAdapter moneyFragmentsAdapter;
    ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_money, container, false);

        // Enable the options menu
        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        moneyFragmentsAdapter = new MoneyFragmentsAdapter(this);
        viewPager = view.findViewById(R.id.money_pager);
        viewPager.setAdapter(moneyFragmentsAdapter);

        TabLayout tabLayout = view.findViewById(R.id.money_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getTabName(position))
        ).attach();
    }

    private String getTabName(int position) {
        switch(position) {
            case 0:
                return "Balance";
            case 1:
                return "Channels";
            case 2:
                return "Transactions";
            default:
                return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.wallet_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Set the refresh button action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.wallet_menu_backup:
                // TODO: backup seed words
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
