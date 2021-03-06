package io.github.yzernik.squeakand.ui.lightningnode;

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

public class LightningNodeFragment extends Fragment {
    LightningNodeFragmentsAdapter lightningNodeFragmentsAdapter;
    ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lightning_node, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String pubkey = null;
        String host = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            pubkey = this.getArguments().getString("pubkey");
            host = this.getArguments().getString("host");
        }

        lightningNodeFragmentsAdapter = new LightningNodeFragmentsAdapter(this, pubkey, host);
        viewPager = view.findViewById(R.id.lightning_node_pager);
        viewPager.setAdapter(lightningNodeFragmentsAdapter);

        TabLayout tabLayout = view.findViewById(R.id.lightning_node_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getTabName(position))
        ).attach();
    }

    private String getTabName(int position) {
        switch(position) {
            case 0:
                return "Connection";
            case 1:
                return "Channels";
            default:
                return null;
        }
    }
}

