package io.github.yzernik.squeakand.blockchain;

import androidx.core.util.Pair;

import org.bitcoinj.core.Sha256Hash;

public interface Blockchain {
    public Pair<Sha256Hash, Integer> getLatestBlock();
    public Sha256Hash getBlockHash(long blockHeight) throws BlockchainException;
}
