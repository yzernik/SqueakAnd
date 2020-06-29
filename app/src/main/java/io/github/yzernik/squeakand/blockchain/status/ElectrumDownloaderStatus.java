package io.github.yzernik.squeakand.blockchain.status;

import org.bitcoinj.core.Block;

import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;
import io.github.yzernik.squeakand.blockchain.ServerUpdate;

public interface ElectrumDownloaderStatus {

    ElectrumServerAddress getServerAddress();

    ServerUpdate.ConnectionStatus getConnectionStatus();

    BlockInfo getLatestBlockInfo();

}
