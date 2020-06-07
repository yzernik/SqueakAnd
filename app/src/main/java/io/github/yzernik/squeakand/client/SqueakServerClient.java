package io.github.yzernik.squeakand.client;

import com.google.protobuf.ByteString;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.networkparameters.NetworkParameters;
import io.github.yzernik.squeaklib.core.Squeak;
import io.github.yzernik.squeaklib.core.SqueakSerializer;
import io.github.yzernik.squeakserver.GetSqueakReply;
import io.github.yzernik.squeakserver.GetSqueakRequest;
import io.github.yzernik.squeakserver.LookupSqueaksReply;
import io.github.yzernik.squeakserver.LookupSqueaksRequest;
import io.github.yzernik.squeakserver.PostSqueakReply;
import io.github.yzernik.squeakserver.PostSqueakRequest;
import io.github.yzernik.squeakserver.SqueakServerGrpc;
import io.grpc.Channel;

public class SqueakServerClient {
    private static final Logger logger = Logger.getLogger(SqueakServerClient.class.getName());

    private final SqueakServerGrpc.SqueakServerBlockingStub blockingStub;
    private final SqueakServerGrpc.SqueakServerStub asyncStub;

    public SqueakServerClient(Channel channel) {
        this.blockingStub = SqueakServerGrpc.newBlockingStub(channel);
        this.asyncStub = SqueakServerGrpc.newStub(channel);
    }

    public List<Sha256Hash> lookupSqueaks(List<String> addresses, int minBlock, int maxBlock) {
        logger.info("*** LookupSqueaks: addresses: " + addresses);

        LookupSqueaksRequest request = LookupSqueaksRequest.newBuilder()
                .addAllAddresses(addresses)
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

    public Squeak getSqueak(Sha256Hash hash) {
        logger.info("*** GetSqueak: hash: " + hash);

        ByteString squeakHashBytes = ByteString.copyFrom(hash.getBytes());
        GetSqueakRequest request = GetSqueakRequest.newBuilder()
                .setHash(squeakHashBytes)
                .build();

        GetSqueakReply reply = blockingStub.getSqueak(request);
        io.github.yzernik.squeakserver.Squeak squeakMessage = reply.getSqueak();
        byte[] squeakBytes = squeakMessage.getSerializedSqueak().toByteArray();
        SqueakSerializer squeakSerializer = new SqueakSerializer(NetworkParameters.getNetworkParameters(), true);
        return squeakSerializer.makeSqueak(squeakBytes);
    }

    public Sha256Hash postSqueak(Squeak squeak) {
        logger.info("*** PostSqueak: squeak: " + squeak);

        ByteString squeakHashBytes = ByteString.copyFrom(squeak.getHash().getBytes());
        ByteString squeakBytes = ByteString.copyFrom(squeak.bitcoinSerialize());

        io.github.yzernik.squeakserver.Squeak squeakMessage = io.github.yzernik.squeakserver.Squeak.newBuilder()
                .setHash(squeakHashBytes)
                .setSerializedSqueak(squeakBytes)
                .build();

        PostSqueakRequest request = PostSqueakRequest.newBuilder()
                .setSqueak(squeakMessage)
                .build();

        PostSqueakReply reply = blockingStub.postSqueak(request);

        ByteString hashReplyBytes = reply.getHash();
        return Sha256Hash.wrap(hashReplyBytes.toByteArray());
    }

}