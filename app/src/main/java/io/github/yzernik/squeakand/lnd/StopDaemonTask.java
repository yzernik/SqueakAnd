package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class StopDaemonTask {

    public static Future<Rpc.StopResponse> stopDaemon(LndClient lndClient) {
        CompletableFuture<Rpc.StopResponse> completableFuture = new CompletableFuture<>();

        lndClient.stop(new LndClient.StopCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.StopResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
