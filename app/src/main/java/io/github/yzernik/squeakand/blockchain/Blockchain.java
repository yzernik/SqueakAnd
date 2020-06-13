package io.github.yzernik.squeakand.blockchain;

import org.bitcoinj.core.Block;

public interface Blockchain {
    BlockInfo getLatestBlockInfo();
    Block getBlock(int blockHeight) throws BlockchainException;
}
