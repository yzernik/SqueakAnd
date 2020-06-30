package io.github.yzernik.squeakand;

public class DataResult<T> {

    private final T response;
    private final Throwable e;

    private DataResult(T response, Throwable e) {
        this.response = response;
        this.e = e;
    }

    public static <T> DataResult ofSuccess(T response) {
        return new DataResult(response, null);
    }

    public static DataResult ofFailure(Throwable e) {
        return new DataResult(null, e);
    }

    public boolean isSuccess() {
        return response != null;
    }

    public boolean isFailure() {
        return e != null;
    }

    public T getResponse() {
        return response;
    }

    public Throwable getError() {
        return e;
    }

}
