package io.github.yzernik.squeakand.server;


import java.util.Objects;

public class SqueakServerAddress {

    private final String host;
    private final int port;

    public SqueakServerAddress(String host, int port) {
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
        return host + ":" + port;
    }

    public static SqueakServerAddress fromString(String s) {
        String[] pieces = s.split(",");
        if (pieces.length != 2) {
            throw new IllegalArgumentException();
        }
        String host = pieces[0];
        int port = Integer.parseInt(pieces[1]);
        return new SqueakServerAddress(host, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqueakServerAddress that = (SqueakServerAddress) o;
        return port == that.port &&
                host.equals(that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
