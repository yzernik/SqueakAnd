package io.github.yzernik.squeakand.ui.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.net.InetSocketAddress;
import java.util.List;

import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;
import io.github.yzernik.squeakand.blockchain.ElectrumServersRepository;

public class BlockchainModel extends ViewModel {

    private ElectrumBlockchainRepository blockchainRepository;
    private ElectrumServersRepository serversRepository;

    public BlockchainModel() {
        blockchainRepository = ElectrumBlockchainRepository.getRepository();
        serversRepository = ElectrumServersRepository.getRepository();
        serversRepository.initialize();
    }

    public LiveData<ElectrumServerAddress> getElectrumServerAddress() {
        return blockchainRepository.getServerAddress();
    }

    public LiveData<BlockInfo> getBlockInfo() {
        return blockchainRepository.getLatestBlock();
    }

    public void setElectrumServerAddress(ElectrumServerAddress electrumServerAddress) {
        blockchainRepository.setServer(electrumServerAddress);
    }

    public LiveData<ElectrumBlockchainRepository.ConnectionStatus> getConnectionStatus() {
        return blockchainRepository.getConnectionStatus();
    }

    public LiveData<List<InetSocketAddress>> getServers() {
        return serversRepository.getElectrumServers();
    }

}
