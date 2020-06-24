package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class ListChannelsTask {
    public static Future<Rpc.ListChannelsResponse> listChannels(LndClient lndClient) {
        CompletableFuture<Rpc.ListChannelsResponse> completableFuture = new CompletableFuture<>();

        lndClient.listChannels(new LndClient.ListChannelsCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.ListChannelsResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
