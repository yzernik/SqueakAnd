package io.github.yzernik.squeakand.blockchain;

import org.bitcoinj.core.Sha256Hash;


public class DummyBlockchain implements Blockchain {
    private static final Sha256Hash GENESIS_BLOCK_HASH = Sha256Hash.wrap("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b");
    private static final int GENESIS_BLOCK_HEIGHT = 0;

    @Override
    public BlockInfo getLatestBlock() {
        return new BlockInfo(GENESIS_BLOCK_HASH, GENESIS_BLOCK_HEIGHT);
    }

    @Override
    public Sha256Hash getBlockHash(int blockHeight) throws BlockchainException {
        if (blockHeight == GENESIS_BLOCK_HEIGHT) {
            return GENESIS_BLOCK_HASH;
        }
        throw new BlockchainException("Unable to find block with height: " + blockHeight);
    }
}