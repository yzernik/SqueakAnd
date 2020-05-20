package io.github.yzernik.squeakand.blockchain;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

public interface BlockchainRepository {
    LiveData<Pair<Sha256Hash, Integer>> getLatestBlock();
    LiveData<Sha256Hash> getBlockHash(int blockHeight);
}
