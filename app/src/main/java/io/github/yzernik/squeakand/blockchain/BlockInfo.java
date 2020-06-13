package io.github.yzernik.squeakand.blockchain;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

public class BlockInfo {

    private Block block;
    private int height;

    public BlockInfo(Block block, int height) {
        this.block = block;
        this.height = height;
    }

    public Block getBlock() {
        return block;
    }

    public Sha256Hash getHash() {
        return block.getHash();
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "BlockInfo(" +
                "hash: " + getHash() + ", " +
                "height: " + height +
                ")";
    }
}
