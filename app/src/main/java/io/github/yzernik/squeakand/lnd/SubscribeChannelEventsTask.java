package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class SubscribeChannelEventsTask {
    public static Future<String> subscribeChannelEvents(LndClient lndClient, LndClient.SubscribeChannelEventsRecvStream subscribeChannelEventsRecvStream) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        lndClient.subscribeChannelEvents(new LndClient.SubscribeChannelEventsRecvStream() {
            @Override
            public void onError(Exception e) {
                // Complete the future with an exception.
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onUpdate(Rpc.ChannelEventUpdate channelEventUpdate) {
                // Continue updating the recv stream.
                subscribeChannelEventsRecvStream.onUpdate(channelEventUpdate);
            }
        });

        return completableFuture;
    }
}
