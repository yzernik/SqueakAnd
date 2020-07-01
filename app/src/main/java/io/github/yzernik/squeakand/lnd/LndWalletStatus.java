package io.github.yzernik.squeakand.lnd;

public class LndWalletStatus {

    private boolean daemonRunning;
    private boolean walletExists;
    private boolean rpcReady;

    public LndWalletStatus() {
        this.daemonRunning = false;
        this.walletExists = false;
        this.rpcReady = false;
    }

    public boolean isDaemonRunning() {
        return daemonRunning;
    }

    public boolean isWalletExists() {
        return walletExists;
    }

    public boolean isRpcReady() {
        return rpcReady;
    }

    public void setDaemonRunning(boolean daemonRunning) {
        this.daemonRunning = daemonRunning;
    }

    public void setWalletExists(boolean walletExists) {
        this.walletExists = walletExists;
    }

    public void setRpcReady(boolean rpcReady) {
        this.rpcReady = rpcReady;
    }

    @Override
    public String toString() {
        return "LndWalletStatus{" +
                "daemonRunning=" + daemonRunning +
                ", walletExists=" + walletExists +
                ", rpcReady=" + rpcReady +
                '}';
    }
}
