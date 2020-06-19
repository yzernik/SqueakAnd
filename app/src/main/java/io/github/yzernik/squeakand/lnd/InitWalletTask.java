package io.github.yzernik.squeakand.lnd;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Walletunlocker;

public class InitWalletTask {

    public static Future<Walletunlocker.InitWalletResponse> initWallet(LndClient lndClient, String password, List<String> seedWords) {
        CompletableFuture<Walletunlocker.InitWalletResponse> completableFuture = new CompletableFuture<>();

        lndClient.initWallet(password, seedWords, new LndClient.InitWalletCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Walletunlocker.InitWalletResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
