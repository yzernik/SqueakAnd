package io.github.yzernik.squeakand.ui.blockchain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;

public class BlockchainModel extends ViewModel {

    private ElectrumBlockchainRepository blockchainRepository;

    public BlockchainModel() {
        blockchainRepository = ElectrumBlockchainRepository.getRepository();
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

}
