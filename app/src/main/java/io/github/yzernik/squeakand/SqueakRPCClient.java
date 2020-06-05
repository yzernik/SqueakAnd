package io.github.yzernik.squeakand;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.github.yzernik.squeakserver.LookupSqueaksReply;
import io.github.yzernik.squeakserver.LookupSqueaksRequest;
import io.github.yzernik.squeakserver.SqueakServerGrpc;
import io.grpc.Channel;

public class SqueakRPCClient {
    private static final Logger logger = Logger.getLogger(SqueakRPCClient.class.getName());

    private final SqueakServerGrpc.SqueakServerBlockingStub blockingStub;
    private final SqueakServerGrpc.SqueakServerStub asyncStub;

    public SqueakRPCClient(Channel channel) {
        this.blockingStub = SqueakServerGrpc.newBlockingStub(channel);
        this.asyncStub = SqueakServerGrpc.newStub(channel);
    }

    public List<Sha256Hash> lookupSqueaks(List<String> addresses, int minBlock, int maxBlock) {
        logger.info("*** LookupSqueaks: addresses: " + addresses);

        LookupSqueaksRequest request = LookupSqueaksRequest.newBuilder()
                .setAddresses(0, addresses.get(0))
                .setMinBlock(minBlock)
                .setMaxBlock(maxBlock)
                .build();

        LookupSqueaksReply reply = blockingStub.lookupSqueaks(request);

        List<Sha256Hash> hashes = reply.getHashesList()
                .stream()
                .map(hashStr -> {
            return Sha256Hash.wrap(hashStr.toByteArray());
        })
                .collect(Collectors.toList());

        return hashes;
    }
}
