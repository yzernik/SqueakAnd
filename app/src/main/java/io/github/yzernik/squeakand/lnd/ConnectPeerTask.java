package io.github.yzernik.squeakand.lnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import lnrpc.Rpc;

public class ConnectPeerTask {
    public static Future<Rpc.ConnectPeerResponse> connectPeer(String pubkey, String host, LndClient lndClient) {
        CompletableFuture<Rpc.ConnectPeerResponse> completableFuture = new CompletableFuture<>();

        lndClient.connectPeer(pubkey, host, new LndClient.ConnectPeerCallBack() {
            @Override
            public void onError(Exception e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Rpc.ConnectPeerResponse response) {
                completableFuture.complete(response);
            }
        });

        return completableFuture;
    }

}
