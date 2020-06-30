package io.github.yzernik.squeakand.ui.transactions;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.lnd.LndRepository;
import lnrpc.Rpc;

public class TransactionsModel extends AndroidViewModel {

    private LndRepository lndRepository;

    public TransactionsModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
    }

    LiveData<List<Rpc.Transaction>> listTransactions() {
        LiveData<DataResult<Rpc.TransactionDetails>> liveTransactionsResult = lndRepository.getTransactions(0, -1);
        return Transformations.map(liveTransactionsResult, transactionsResult -> {
            if (transactionsResult.isFailure()) {
                return null;
            }
            Rpc.TransactionDetails transactionDetails = transactionsResult.getResponse();
            return transactionDetails.getTransactionsList();
        });
    }

}
