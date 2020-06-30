package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class GetTransactionsTask {
    public static Future<Rpc.TransactionDetails> getTransactions(int startHeight, int endHeight, LndClient lndClient) {
        CompletableFuture<Rpc.TransactionDetails> completableFuture = new CompletableFuture<>();

        lndClient.getTransactions(startHeight, endHeight, new LndClient.GetTransactionsCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.TransactionDetails response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }
}



