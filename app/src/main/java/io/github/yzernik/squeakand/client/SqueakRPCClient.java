package io.github.yzernik.squeakand.client;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class SqueakRPCClient {

    private final String host;
    private final int port;

    public SqueakRPCClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public List<Sha256Hash> lookupSqueaks(List<String> addresses, int minBlock, int maxBlock) {
        String target =  String.format("{}:{}", this.host, this.port);
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        try {
            SqueakServerClient client = new SqueakServerClient(channel);
            return client.lookupSqueaks(addresses, minBlock, maxBlock);
        } finally {
            channel.shutdown();
        }

    }


}
