package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class NewAddressTask {
    public static Future<Rpc.NewAddressResponse> newAddress(LndClient lndClient) {
        CompletableFuture<Rpc.NewAddressResponse> completableFuture = new CompletableFuture<>();

        lndClient.newAddress(new LndClient.NewAddressCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.NewAddressResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
