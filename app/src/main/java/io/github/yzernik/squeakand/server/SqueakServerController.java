package io.github.yzernik.squeakand.server;

import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.client.SqueakRPCClient;
import io.github.yzernik.squeakand.squeaks.SqueaksController;
import io.github.yzernik.squeaklib.core.Squeak;

public class SqueakServerController {

    private static final int DEFAULT_MIN_BLOCK = 0;
    private static final int DEFAULT_MAX_BLOCK = 1000000000;

    private final SqueakServerAddress serverAddress;
    private final SqueaksController squeaksController;
    private final SqueakRPCClient client;

    public SqueakServerController(SqueakServerAddress serverAddress, SqueaksController squeaksController) {
        this.serverAddress = serverAddress;
        this.squeaksController = squeaksController;
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
        // Download the buy offer to the server.
        Offer offer = client.buySqueak(hash);
        // TODO: save the offer in the database.
        Log.i(getClass().getName(), "Got offer: " + offer + " from server: " + serverAddress);
        return offer;
    }

    public void uploadSync(List<String> uploadAddresses, int minBlock, int maxBlock) {
        Log.i(getClass().getName(), "Calling uploadSync...");

        Set<Sha256Hash> remoteHashes = getRemoteHashes(uploadAddresses, minBlock, maxBlock);
        Log.i(getClass().getName(), "Upload server number of hashes: " + remoteHashes.size());

        Set<Sha256Hash> localHashes = getLocalHashes(uploadAddresses, minBlock, maxBlock);
        Log.i(getClass().getName(), "Upload local number of hashes: " + localHashes.size());

        // For every local hash not in server hashes, upload.
        localHashes.removeAll(remoteHashes);
        Log.i(getClass().getName(), "Uploading number of squeaks: " + localHashes.size());
        for (Sha256Hash squeakHash: localHashes) {
            Squeak squeak = squeaksController.fetchSqueakWithProfileByHash(squeakHash).squeakEntry.getSqueak();
            upload(squeak);
        }
    }

    public void downloadSync(List<String> downloadAddresses, int minBlock, int maxBlock) {
        Log.i(getClass().getName(), "Calling downloadSync...");

        Set<Sha256Hash> remoteHashes = getRemoteHashes(downloadAddresses, minBlock, maxBlock);
        Log.i(getClass().getName(), "Download server number of hashes: " + remoteHashes.size());

        Set<Sha256Hash> localHashes = getLocalHashes(downloadAddresses, minBlock, maxBlock);
        Log.i(getClass().getName(), "Download local number of hashes: " + localHashes.size());

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


    private Set<Sha256Hash> getLocalHashes(List<String> uploadAddresses, int minBlock, int maxBlock) {
        Set<Sha256Hash> localHashes = new HashSet<>();
        for (String address: uploadAddresses) {
            // TODO: include block range in DAO method.
            List<SqueakEntry> squeakEntries = squeaksController.fetchSqueaksByAddress(address);
            for (SqueakEntry squeakEntry: squeakEntries) {
                localHashes.add(squeakEntry.hash);
            }
        }
        return localHashes;
    }
}
