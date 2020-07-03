package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class SubscribePeerEventsTask {
    public static Future<String> subscribePeerEvents(LndClient lndClient, LndClient.SubscribePeerEventsRecvStream subscribePeerEventsRecvStream) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        lndClient.subscribePeerEvents(new LndClient.SubscribePeerEventsRecvStream() {
            @Override
            public void onError(Exception e) {
                // Complete the future with an exception.
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onUpdate(Rpc.PeerEvent update) {
                // Continue updating the recv stream.
                subscribePeerEventsRecvStream.onUpdate(update);
            }
        });

        return completableFuture;
    }
}
