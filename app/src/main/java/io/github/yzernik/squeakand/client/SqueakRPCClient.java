package io.github.yzernik.squeakand.client;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.yzernik.squeaklib.core.Squeak;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class SqueakRPCClient {

    private final String host;
    private final int port;
    private final String target;

    public SqueakRPCClient(String host, int port) {
        this.host = host;
        this.port = port;
        target =  String.format("%s:%d", this.host, this.port);
    }

    private ManagedChannel getChannel() {
        return ManagedChannelBuilder
                .forTarget(target)
                .usePlaintext()
                .build();
    }

    public List<Sha256Hash> lookupSqueaks(List<String> addresses, int minBlock, int maxBlock) {
        ManagedChannel channel = getChannel();
        try {
            SqueakServerClient client = new SqueakServerClient(channel);
            return client.lookupSqueaks(addresses, minBlock, maxBlock);
        } finally {
            channel.shutdown();
        }
    }

    public Squeak getSqueak(Sha256Hash hash) {
        ManagedChannel channel = getChannel();
        try {
            SqueakServerClient client = new SqueakServerClient(channel);
            return client.getSqueak(hash);
        } finally {
            channel.shutdown();
        }
    }

    public Sha256Hash postSqueak(Squeak squeak) {
        ManagedChannel channel = getChannel();
        try {
            SqueakServerClient client = new SqueakServerClient(channel);
            return client.postSqueak(squeak);
        } finally {
            channel.shutdown();
        }
    }

}
