package io.github.yzernik.squeakand.blockchain;

import org.bitcoinj.core.Block;
import org.bitcoinj.params.MainNetParams;


public class DummyBlockchain implements Blockchain {
    private static final Block GENESIS_BLOCK = MainNetParams.get().getGenesisBlock();
    private static final int GENESIS_BLOCK_HEIGHT = 0;

    @Override
    public BlockInfo getLatestBlockInfo() {
        return new BlockInfo(GENESIS_BLOCK, GENESIS_BLOCK_HEIGHT);
    }

    @Override
    public Block getBlock(int blockHeight) throws BlockchainException {
        if (blockHeight == GENESIS_BLOCK_HEIGHT) {
            return GENESIS_BLOCK;
        }
        throw new BlockchainException("Unable to find block with height: " + blockHeight);
    }
}