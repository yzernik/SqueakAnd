package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class OpenChannelTask {
    public static Future<Rpc.ChannelPoint> openChannel(String pubkey, long amount, LndClient lndClient) {
        CompletableFuture<Rpc.ChannelPoint> completableFuture = new CompletableFuture<>();

        lndClient.openChannel(pubkey, amount, new LndClient.OpenChannelCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.ChannelPoint response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
