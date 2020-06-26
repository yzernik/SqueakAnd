package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class ListPeersTask {
    public static Future<Rpc.ListPeersResponse> listPeers(LndClient lndClient) {
        CompletableFuture<Rpc.ListPeersResponse> completableFuture = new CompletableFuture<>();

        lndClient.listPeers(new LndClient.ListPeersCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.ListPeersResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
