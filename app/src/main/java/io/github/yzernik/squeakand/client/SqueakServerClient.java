package io.github.yzernik.squeakand.client;

import com.google.protobuf.ByteString;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.networkparameters.NetworkParameters;
import io.github.yzernik.squeaklib.core.Squeak;
import io.github.yzernik.squeaklib.core.SqueakSerializer;
import io.github.yzernik.squeakserver.BuySqueakReply;
import io.github.yzernik.squeakserver.BuySqueakRequest;
import io.github.yzernik.squeakserver.GetSqueakReply;
import io.github.yzernik.squeakserver.GetSqueakRequest;
import io.github.yzernik.squeakserver.LookupSqueaksReply;
import io.github.yzernik.squeakserver.LookupSqueaksRequest;
import io.github.yzernik.squeakserver.PostSqueakReply;
import io.github.yzernik.squeakserver.PostSqueakRequest;
import io.github.yzernik.squeakserver.SqueakBuyOffer;
import io.github.yzernik.squeakserver.SqueakServerGrpc;
import io.grpc.Channel;

import static org.bitcoinj.core.Utils.HEX;

public class SqueakServerClient {
    private static final Logger logger = Logger.getLogger(SqueakServerClient.class.getName());

    private static final int LOOKUP_REQUEST_TIMEOUT_S = 10;
    private static final int POST_REQUEST_TIMEOUT_S = 10;
    private static final int GET_REQUEST_TIMEOUT_S = 10;
    private static final int BUY_REQUEST_TIMEOUT_S = 10;

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

        LookupSqueaksReply reply = blockingStub
                .withDeadlineAfter(LOOKUP_REQUEST_TIMEOUT_S, TimeUnit.SECONDS)
                .lookupSqueaks(request);

        List<Sha256Hash> hashes = reply.getHashesList()
                .stream()
                .map(hashStr -> Sha256Hash.wrap(hashStr.toByteArray()))
                .collect(Collectors.toList());
        return hashes;
    }

    public Squeak getSqueak(Sha256Hash hash) {
        logger.info("*** GetSqueak: hash: " + hash);

        ByteString squeakHashBytes = ByteString.copyFrom(hash.getBytes());
        GetSqueakRequest request = GetSqueakRequest.newBuilder()
                .setHash(squeakHashBytes)
                .build();

        GetSqueakReply reply = blockingStub
                .withDeadlineAfter(GET_REQUEST_TIMEOUT_S, TimeUnit.SECONDS)
                .getSqueak(request);
        io.github.yzernik.squeakserver.Squeak squeakMessage = reply.getSqueak();
        byte[] squeakBytes = squeakMessage.getSerializedSqueak().toByteArray();
        SqueakSerializer squeakSerializer = new SqueakSerializer(NetworkParameters.getNetworkParameters(), true);
        return squeakSerializer.makeSqueak(squeakBytes);
    }

    public void postSqueak(Squeak squeak) {
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

        PostSqueakReply reply = blockingStub
                .withDeadlineAfter(POST_REQUEST_TIMEOUT_S, TimeUnit.SECONDS)
                .postSqueak(request);
        return;
    }

    public GetOfferResponse buySqueak(Sha256Hash hash, byte[] challenge) {
        logger.info("*** BuySqueak: hash: " + hash + ", challenge: " + HEX.encode(challenge));

        ByteString squeakHashBytes = ByteString.copyFrom(hash.getBytes());
        ByteString challengeBytes = ByteString.copyFrom(challenge);
        BuySqueakRequest request = BuySqueakRequest.newBuilder()
                .setHash(squeakHashBytes)
                .setChallenge(challengeBytes)
                .build();

        BuySqueakReply reply = blockingStub
                .withDeadlineAfter(BUY_REQUEST_TIMEOUT_S, TimeUnit.SECONDS)
                .buySqueak(request);
        SqueakBuyOffer buyOfferMessage = reply.getOffer();

        Sha256Hash squeakHash = Sha256Hash.wrap(buyOfferMessage.getSqueakHash().toByteArray());
        byte[] keyCipher = buyOfferMessage.getKeyCipher().toByteArray();
        byte[] iv = buyOfferMessage.getIv().toByteArray();
        Sha256Hash preimageHash = Sha256Hash.wrap(buyOfferMessage.getPreimageHash().toByteArray());
        long amount = buyOfferMessage.getAmount();
        String paymentRequest = buyOfferMessage.getPaymentRequest();
        String pubkey = buyOfferMessage.getPubkey();
        String host = buyOfferMessage.getHost();
        int port = buyOfferMessage.getPort();

        Offer offer = new Offer(
                squeakHash,
                keyCipher,
                iv,
                preimageHash,
                amount,
                paymentRequest,
                pubkey,
                host,
                port,
                null
        );

        return new GetOfferResponse(offer, buyOfferMessage.getProof().toByteArray());
    }

}
