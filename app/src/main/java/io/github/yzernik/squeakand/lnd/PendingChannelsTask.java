package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class PendingChannelsTask {
    public static Future<Rpc.PendingChannelsResponse> pendingChannels(LndClient lndClient) {
        CompletableFuture<Rpc.PendingChannelsResponse> completableFuture = new CompletableFuture<>();

        lndClient.pendingChannels(new LndClient.PendingChannelsCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.PendingChannelsResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }
}
