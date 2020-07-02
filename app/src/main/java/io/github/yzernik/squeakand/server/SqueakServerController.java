package io.github.yzernik.squeakand.server;

import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.client.SqueakRPCClient;
import io.github.yzernik.squeakand.squeaks.SqueaksController;
import io.github.yzernik.squeaklib.core.Squeak;

public class SqueakServerController {

    private static final int DEFAULT_MIN_BLOCK = 0;
    private static final int DEFAULT_MAX_BLOCK = 1000000000;

    private final SqueakServer server;
    private final SqueaksController squeaksController;
    private final SqueakRPCClient client;

    public SqueakServerController(SqueakServer server, SqueaksController squeaksController) {
        this.server = server;
        this.squeaksController = squeaksController;
        SqueakServerAddress serverAddress = server.serverAddress;
        client = new SqueakRPCClient(serverAddress.getHost(), serverAddress.getPort());
    }

    public void upload(Squeak squeak) {
        // Upload the squeak to the server.
        client.postSqueak(squeak);
    }

    public Squeak download(Sha256Hash hash) {
        // Download the squeak to the server.
        Squeak squeak = client.getSqueak(hash);
        squeaksController.save(squeak);
        return squeak;
    }

    public Offer getOffer(Sha256Hash hash) {
        // Check if there is already an offer in the local database
        Offer localOffer = squeaksController.getOfferForSqueakAndServer(hash, server.getAddress());
        if (localOffer != null) {
            Log.i(getClass().getName(), "Got offer: " + localOffer + " from local database.");
            // TODO: If the offer is expired (and not already paid and valid), delete it.
            if (!localOffer.getHasValidPreimage()) {
                squeaksController.deleteOffer(localOffer);
            } else {
                return localOffer;
            }
        }

        // Download the buy offer to the server.
        Offer offer = client.buySqueak(hash);
        offer.setSqueakServerAddress(server.getAddress());
        Log.i(getClass().getName(), "Got offer: " + offer + " from server: " + server.serverAddress);
        squeaksController.saveOffer(offer);
        return offer;
    }

    public void uploadSync(List<String> uploadAddresses, int minBlock, int maxBlock) {
        Set<Sha256Hash> remoteHashes = getRemoteHashes(uploadAddresses, minBlock, maxBlock);
        Set<Sha256Hash> localHashes = getLocalHashes(uploadAddresses, minBlock, maxBlock, true);

        // For every local hash not in server hashes, upload.
        localHashes.removeAll(remoteHashes);
        Log.i(getClass().getName(), "Uploading number of squeaks: " + localHashes.size());
        for (Sha256Hash squeakHash: localHashes) {
            Squeak squeak = squeaksController.fetchSqueakWithProfileByHash(squeakHash).squeakEntry.getSqueak();
            upload(squeak);
        }
    }

    public void downloadSync(List<String> downloadAddresses, int minBlock, int maxBlock) {
        Set<Sha256Hash> remoteHashes = getRemoteHashes(downloadAddresses, minBlock, maxBlock);
        Set<Sha256Hash> localHashes = getLocalHashes(downloadAddresses, minBlock, maxBlock);

        // For every server hash not in remote hashes, download.
        remoteHashes.removeAll(localHashes);
        Log.i(getClass().getName(), "Downloading number of squeaks: " + remoteHashes.size());
        for (Sha256Hash hash: remoteHashes) {
            download(hash);
        }
    }

    private Set<Sha256Hash> getRemoteHashes(List<String> uploadAddresses, int minBlock, int maxBlock) {
        List<Sha256Hash> hashList = client.lookupSqueaks(uploadAddresses, minBlock, maxBlock);
        return new HashSet<>(hashList);
    }


    private Set<Sha256Hash> getLocalHashes(List<String> uploadAddresses, int minBlock, int maxBlock, boolean onlyUnlocked) {
        Set<Sha256Hash> localHashes = new HashSet<>();
        for (String address: uploadAddresses) {
            // TODO: include block range in DAO method.
            List<SqueakEntry> squeakEntries = squeaksController.fetchSqueaksByAddress(address, minBlock, maxBlock);
            for (SqueakEntry squeakEntry: squeakEntries) {
                if (!onlyUnlocked || squeakEntry.hasDataKey()) {
                    localHashes.add(squeakEntry.hash);
                }
            }
        }
        return localHashes;
    }

    private Set<Sha256Hash> getLocalHashes(List<String> uploadAddresses, int minBlock, int maxBlock) {
        return getLocalHashes(uploadAddresses, minBlock, maxBlock, false);
    }
}
