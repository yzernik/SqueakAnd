package io.github.yzernik.squeakand.ui.createtodo;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.blockchain.BlockInfo;

public class CreateSqueakParams {

    private SqueakProfile squeakProfile;
    private Sha256Hash replyToHash;
    private BlockInfo latestBlock;

    public CreateSqueakParams(SqueakProfile squeakProfile, Sha256Hash replyToHash, BlockInfo latestBlock) {
        this.squeakProfile = squeakProfile;
        this.replyToHash = replyToHash;
        this.latestBlock = latestBlock;
    }

    public SqueakProfile getSqueakProfile() {
        return squeakProfile;
    }

    public Sha256Hash getReplyToHash() {
        return replyToHash;
    }

    public BlockInfo getLatestBlockk() {
        return latestBlock;
    }

    @Override
    public String toString() {
        return "CreateSqueakParams(" +
                "squeakProfile: " + squeakProfile + ", " +
                "replyToHash: " + replyToHash + ", " +
                "latestBlock: " + latestBlock +
                ")";
    }

}
