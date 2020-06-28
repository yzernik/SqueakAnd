package io.github.yzernik.squeakand.lnd;

import lnrpc.Rpc;

public class ConnectPeerResult {

    private final Rpc.ConnectPeerResponse response;
    private final Throwable e;

    private ConnectPeerResult(Rpc.ConnectPeerResponse response, Throwable e) {
        this.response = response;
        this.e = e;
    }

    public static ConnectPeerResult ofSuccess(Rpc.ConnectPeerResponse response) {
        return new ConnectPeerResult(response, null);
    }

    public static ConnectPeerResult ofFailure(Throwable e) {
        return new ConnectPeerResult(null, e);
    }

    public boolean isSuccess() {
        return response != null;
    }

    public Rpc.ConnectPeerResponse getResponse() {
        return response;
    }

    public Throwable getError() {
        return e;
    }

}
