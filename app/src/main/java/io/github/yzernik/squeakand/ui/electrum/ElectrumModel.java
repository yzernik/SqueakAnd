package io.github.yzernik.squeakand.ui.electrum;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;
import io.github.yzernik.squeakand.blockchain.ElectrumServersRepository;
import io.github.yzernik.squeakand.blockchain.ServerUpdate;

public class ElectrumModel extends AndroidViewModel {

    private ElectrumBlockchainRepository blockchainRepository;
    private ElectrumServersRepository serversRepository;
    // private Preferences preferences;

    public ElectrumModel(Application application) {
        super(application);
        blockchainRepository = ElectrumBlockchainRepository.getRepository(application);
        serversRepository = ElectrumServersRepository.getRepository();
    }

/*
    public LiveData<ElectrumServerAddress> getElectrumServerAddress() {
        return blockchainRepository.getServerAddress();
    }*/

/*
    public LiveData<BlockInfo> getLatestBlock() {
        return blockchainRepository.getLatestBlock();
    }
*/

    public void setElectrumServerAddress(ElectrumServerAddress electrumServerAddress) {
        blockchainRepository.setServer(electrumServerAddress);
        serversRepository.addPeer(electrumServerAddress);
    }

/*    public LiveData<ServerUpdate.ConnectionStatus> getConnectionStatus() {
        return blockchainRepository.getConnectionStatus();
    }*/

    public LiveData<List<ElectrumServerAddress>> getServers() {
        return serversRepository.getElectrumServers();
    }

    public LiveData<ServerUpdate> getServerUpdate() {
        return blockchainRepository.getServerUpdate();
    }



}
