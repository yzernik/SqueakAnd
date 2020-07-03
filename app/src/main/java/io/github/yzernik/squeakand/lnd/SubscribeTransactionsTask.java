package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class SubscribeTransactionsTask {
    public static Future<String> subscribeTransactions(int startHeight, int endHeight, LndClient lndClient, LndClient.SubscribeTransactionsRecvStream subscribeTransactionsRecvStream) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        lndClient.subscribeTransactions(startHeight, endHeight, new LndClient.SubscribeTransactionsRecvStream() {
            @Override
            public void onError(Exception e) {
                // Complete the future with an exception.
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onUpdate(Rpc.Transaction transaction) {
                // Continue updating the recv stream.
                subscribeTransactionsRecvStream.onUpdate(transaction);
            }
        });

        return completableFuture;
    }
}
