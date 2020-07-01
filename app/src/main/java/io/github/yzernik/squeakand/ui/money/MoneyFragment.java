package io.github.yzernik.squeakand.ui.money;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class MoneyFragment extends Fragment {
    MoneyFragmentsAdapter moneyFragmentsAdapter;
    ViewPager2 viewPager;

    private MoneyViewModel moneyViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_money, container, false);

        // Enable the options menu
        setHasOptionsMenu(true);

        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);

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
                showWalletBackupAlert();
                return true;
            case R.id.wallet_menu_delete:
                deleteWallet();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showWalletBackupAlert() {
        String[] seedWords = moneyViewModel.getWalletSeed();
        String seedWordsString = String.join(", ", seedWords);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Wallet backup seed");
        alertDialog.setMessage("Seed words: " + seedWordsString + ".");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void deleteWallet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                moneyViewModel.deleteWallet();
            }
        }).start();
    }


}
