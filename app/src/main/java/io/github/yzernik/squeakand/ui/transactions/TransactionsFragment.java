package io.github.yzernik.squeakand.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class TransactionsFragment extends Fragment implements TransactionListAdapter.ClickListener {

    private TransactionsModel transactionsModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transactions, container, false);

        transactionsModel = new ViewModelProvider(this).get(TransactionsModel.class);

        final RecyclerView recyclerView = root.findViewById(R.id.transactionsRecyclerView);
        final TransactionListAdapter adapter = new TransactionListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        transactionsModel.listTransactions().observe(getViewLifecycleOwner(), new Observer<List<Rpc.Transaction>>() {
            @Override
            public void onChanged(@Nullable final List<Rpc.Transaction> transactions) {
                if (transactions == null) {
                    return;
                }
                // Sort the list by block height
                List<Rpc.Transaction> modifiableTransactions = new ArrayList<>(transactions);
                Collections.sort(modifiableTransactions, (tx1, tx2) -> {
                    long diff = tx2.getTimeStamp() - tx1.getTimeStamp();
                    return (int) (diff / 1000);
                });
                // Update the cached copy of the transactions in the adapter.
                adapter.setProfiles(modifiableTransactions);
            }
        });

        return root;
    }

    @Override
    public void handleItemClick(Rpc.Transaction transaction) {
        // Do nothing.
    }

}
