package io.github.yzernik.squeakand.blockchain;

import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

public interface BlockchainRepository {
    LiveData<BlockInfo> getLatestBlock();
    LiveData<Sha256Hash> getBlockHash(int blockHeight);
}
