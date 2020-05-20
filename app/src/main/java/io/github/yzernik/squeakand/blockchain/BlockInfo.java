package io.github.yzernik.squeakand.blockchain;

import org.bitcoinj.core.Sha256Hash;

public class BlockInfo {

    private Sha256Hash hash;
    private int height;

    public BlockInfo(Sha256Hash hash, int height) {
        this.hash = hash;
        this.height = height;
    }

    public Sha256Hash getHash() {
        return hash;
    }

    public int getHeight() {
        return height;
    }
}
