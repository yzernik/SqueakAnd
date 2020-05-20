package io.github.yzernik.squeakand.blockchain;

import androidx.core.util.Pair;

import org.bitcoinj.core.Sha256Hash;

public interface Blockchain {
    Pair<Sha256Hash, Integer> getLatestBlock();
    Sha256Hash getBlockHash(int blockHeight) throws BlockchainException;
}
