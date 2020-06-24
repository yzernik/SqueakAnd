package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;


public class GetInfoTask {

    public static Future<Rpc.GetInfoResponse> getInfo(LndClient lndClient) {
        CompletableFuture<Rpc.GetInfoResponse> completableFuture = new CompletableFuture<>();

        lndClient.getInfo(new LndClient.GetInfoCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.GetInfoResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
