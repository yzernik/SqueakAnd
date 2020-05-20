package io.github.yzernik.squeakand.ui.createtodo;

import androidx.core.util.Pair;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.SqueakProfile;

public class CreateSqueakParams {

    private SqueakProfile squeakProfile;
    private Sha256Hash replyToHash;
    private Pair<Sha256Hash, Integer> latestBlock;

    public CreateSqueakParams(SqueakProfile squeakProfile, Sha256Hash replyToHash, Pair<Sha256Hash, Integer> latestBlock) {
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

    public Pair<Sha256Hash, Integer> getLatestBlockk() {
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
