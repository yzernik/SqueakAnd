package io.github.yzernik.squeakand.ui.money;

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

public class MoneyFragment extends Fragment {
    MoneyFragmentsAdapter moneyFragmentsAdapter;
    ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money, container, false);
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
}
