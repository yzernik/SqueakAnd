package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Walletunlocker;

public class GenSeedTask {

    public static Future<Walletunlocker.GenSeedResponse> genSeed(LndClient lndClient) {
        CompletableFuture<Walletunlocker.GenSeedResponse> completableFuture = new CompletableFuture<>();

        lndClient.genSeed(new LndClient.GenSeedCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Walletunlocker.GenSeedResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
