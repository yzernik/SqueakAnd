package io.github.yzernik.squeakand.server;

import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.yzernik.squeakand.SqueakDao;
import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.client.SqueakRPCClient;
import io.github.yzernik.squeaklib.core.Squeak;

public class UploaderDownloader {

    private static final int DEFAULT_MIN_BLOCK = 0;
    private static final int DEFAULT_MAX_BLOCK = 1000000000;

    private final SqueakServerAddress serverAddress;
    private final SqueakDao squeakDao;
    private final SqueakRPCClient client;

    public UploaderDownloader(SqueakServerAddress serverAddress, SqueakDao squeakDao) {
        this.serverAddress = serverAddress;
        this.squeakDao = squeakDao;
        client = new SqueakRPCClient(serverAddress.getHost(), serverAddress.getPort());
    }

    public void upload(Squeak squeak) {
        // Upload the squeak to the server.
        client.postSqueak(squeak);
    }

    public Squeak download(Sha256Hash hash) {
        // Download the squeak to the server.
        return client.getSqueak(hash);
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
        for (Sha256Hash hash: localHashes) {
            Squeak squeak = squeakDao.fetchSqueakByHash(hash).getSqueak();
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
            Squeak squeak = download(hash);
            squeakDao.insert(new SqueakEntry(squeak));
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
            List<SqueakEntry> squeakEntries = squeakDao.fetchSqueaksByAddress(address);
            for (SqueakEntry squeakEntry: squeakEntries) {
                localHashes.add(squeakEntry.hash);
            }
        }
        return localHashes;
    }
}
