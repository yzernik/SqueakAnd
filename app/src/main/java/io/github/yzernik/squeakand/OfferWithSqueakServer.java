package io.github.yzernik.squeakand;

import androidx.room.Embedded;


public class OfferWithSqueakServer {

    @Embedded
    public Offer offer;

    @Embedded
    public SqueakServer squeakServer;

}
