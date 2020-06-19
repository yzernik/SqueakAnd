package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Walletunlocker;

public class UnlockWalletTask {

    public static Future<Walletunlocker.UnlockWalletResponse> unlockWallet(LndClient lndClient, String password) {
        CompletableFuture<Walletunlocker.UnlockWalletResponse> completableFuture = new CompletableFuture<>();

        lndClient.unlockWallet(password, new LndClient.UnlockWalletCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Walletunlocker.UnlockWalletResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
