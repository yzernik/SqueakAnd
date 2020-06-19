package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class StartWalletTask {

    public static Future<String> startWallet(LndClient lndClient, String lndDir, String network) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        lndClient.start(lndDir, network, new LndClient.StartCallBack() {
            @Override
            public void onError1(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse1() {
                completableFuture.complete("Started successfully");
            }

            @Override
            public void onError2(Exception e) {
                // Nothing
            }

            @Override
            public void onResponse2() {
                // Nothing
            }
        });

        return completableFuture;
    }


}
