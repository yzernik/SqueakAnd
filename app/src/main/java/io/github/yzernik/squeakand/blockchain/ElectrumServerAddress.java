package io.github.yzernik.squeakand.blockchain;

public class ElectrumServerAddress {

    private final String host;
    private final int port;

    public ElectrumServerAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "ElectrumServerAddress(" +
                "host: " + host + ", " +
                "port: " + port +
                ")";
    }
}
