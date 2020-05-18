package io.github.yzernik.squeakand.ui.createtodo;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.SqueakProfile;

public class CreateSqueakParams {

    private SqueakProfile squeakProfile;
    private Sha256Hash replyToHash;
    // private Sha256Hash blockHash;
    // private int blockHeight;

    public CreateSqueakParams(SqueakProfile squeakProfile, Sha256Hash replyToHash) {
        this.squeakProfile = squeakProfile;
        this.replyToHash = replyToHash;
    }

    public SqueakProfile getSqueakProfile() {
        return squeakProfile;
    }

    public Sha256Hash getReplyToHash() {
        return replyToHash;
    }

}
