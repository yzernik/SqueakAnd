package io.github.yzernik.squeakand.ui.money;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;

public class MoneyFragment extends Fragment {

    private MoneyViewModel moneyViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_money, container, false);

        // Get a new or existing ViewModel from the ViewModelProvider.
        moneyViewModel = new ViewModelProvider(this).get(MoneyViewModel.class);

        moneyViewModel.initWallet();

        return root;
    }

}
