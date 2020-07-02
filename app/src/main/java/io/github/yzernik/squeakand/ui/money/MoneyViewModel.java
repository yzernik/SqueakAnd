package io.github.yzernik.squeakand.ui.money;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.lnd.LndWalletStatus;
import lnrpc.Rpc;

public class MoneyViewModel  extends AndroidViewModel {

    private LndRepository lndRepository;
    private LiveData<LndWalletStatus> liveLndWalletStatus;


    public MoneyViewModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
        this.liveLndWalletStatus = lndRepository.getLndWalletStatus();
    }

    LiveData<Rpc.GetInfoResponse> getInfo() {
        return lndRepository.getInfo();
    }

    LiveData<Rpc.WalletBalanceResponse> walletBalance() {
        return lndRepository.walletBalance();
    }

    LiveData<Rpc.ListChannelsResponse> listChannels() {
        return lndRepository.getLiveChannels();
    }

    LiveData<Rpc.PendingChannelsResponse> pendingChannels() {
        return lndRepository.pendingChannels();
    }

    LiveData<Rpc.ListPeersResponse> listPeers() {
        return lndRepository.listPeers();
    }

    LiveData<DataResult<Rpc.NewAddressResponse>> newAddress() {
        return lndRepository.newAddress();
    }

    LiveData<LndWalletStatus> getLiveLndWalletStatus() {
        return liveLndWalletStatus;
    }

    public String[] getWalletSeed() {
        return lndRepository.getWalletSeedWords();
    }

    public void deleteWallet() {
        lndRepository.deleteWallet();
    }

}
