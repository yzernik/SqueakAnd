package io.github.yzernik.squeakand.blockchain;

import org.bitcoinj.core.Sha256Hash;

public interface Blockchain {
    BlockInfo getLatestBlock();
    Sha256Hash getBlockHash(int blockHeight) throws BlockchainException;
}
