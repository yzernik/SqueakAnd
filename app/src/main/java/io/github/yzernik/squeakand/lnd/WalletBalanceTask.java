package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class WalletBalanceTask {

    public static Future<Rpc.WalletBalanceResponse> walletBalance(LndClient lndClient) {
        CompletableFuture<Rpc.WalletBalanceResponse> completableFuture = new CompletableFuture<>();

        lndClient.walletBalance(new LndClient.WalletBalanceCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.WalletBalanceResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
