package io.github.yzernik.squeakand.client;

import io.github.yzernik.squeakand.Offer;

public class GetOfferResponse {

    private final Offer offer;
    private final byte[] proof;

    public GetOfferResponse(Offer offer, byte[] proof) {
        this.offer = offer;
        this.proof = proof;
    }

    public Offer getOffer() {
        return offer;
    }

    public byte[] getProof() {
        return proof;
    }

}
