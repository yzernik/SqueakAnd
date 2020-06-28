package io.github.yzernik.squeakand.lnd;

import lnrpc.Rpc;

public class LndResult<T> {

    private final T response;
    private final Throwable e;

    private LndResult(T response, Throwable e) {
        this.response = response;
        this.e = e;
    }

    public static <T> LndResult ofSuccess(T response) {
        return new LndResult(response, null);
    }

    public static LndResult ofFailure(Throwable e) {
        return new LndResult(null, e);
    }

    public boolean isSuccess() {
        return response != null;
    }

    public T getResponse() {
        return response;
    }

    public Throwable getError() {
        return e;
    }

}
