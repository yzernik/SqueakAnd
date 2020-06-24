package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class SendPaymentTask {
    public static Future<Rpc.SendResponse> sendPayment(String paymentRequest, LndClient lndClient) {
        CompletableFuture<Rpc.SendResponse> completableFuture = new CompletableFuture<>();

        lndClient.sendPayment(paymentRequest, new LndClient.SendPaymentCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.SendResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
