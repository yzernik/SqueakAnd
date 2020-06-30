package io.github.yzernik.squeakand.ui.money;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.DataResult;
import lnrpc.Rpc;

public class MoneyViewModel  extends AndroidViewModel {

    private LndRepository lndRepository;

    public MoneyViewModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
    }

    LiveData<DataResult<Rpc.GetInfoResponse>> getInfo() {
        return lndRepository.getInfo();
    }

    LiveData<DataResult<Rpc.WalletBalanceResponse>> walletBalance() {
        return lndRepository.walletBalance();
    }

    LiveData<DataResult<Rpc.ListChannelsResponse>> listChannels() {
        return lndRepository.listChannels();
    }

    LiveData<DataResult<Rpc.TransactionDetails>> getTransactions() {
        return lndRepository.getTransactions(0, -1);
    }

    LiveData<DataResult<Rpc.ListPeersResponse>> listPeers() {
        return lndRepository.listPeers();
    }

    LiveData<DataResult<Rpc.NewAddressResponse>> newAddress() {
        return lndRepository.newAddress();
    }

}
