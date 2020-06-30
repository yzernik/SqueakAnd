package io.github.yzernik.squeakand.ui.money;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.lnd.LndResult;
import lnrpc.Rpc;

public class MoneyViewModel  extends AndroidViewModel {

    private LndRepository lndRepository;

    public MoneyViewModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
    }

    LiveData<LndResult<Rpc.GetInfoResponse>> getInfo() {
        return lndRepository.getInfo();
    }

    LiveData<LndResult<Rpc.WalletBalanceResponse>> walletBalance() {
        return lndRepository.walletBalance();
    }

    LiveData<LndResult<Rpc.ListChannelsResponse>> listChannels() {
        return lndRepository.listChannels();
    }

    LiveData<LndResult<Rpc.ListPeersResponse>> listPeers() {
        return lndRepository.listPeers();
    }

    LiveData<LndResult<Rpc.NewAddressResponse>> newAddress() {
        return lndRepository.newAddress();
    }

}
