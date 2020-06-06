package io.github.yzernik.squeakand.server;

import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.SqueakDao;
import io.github.yzernik.squeakand.client.SqueakRPCClient;
import io.github.yzernik.squeaklib.core.Squeak;

public class Uploader {

    private final SqueakServerAddress serverAddress;
    private final SqueakDao squeakDao;
    private final SqueakRPCClient client;

    public Uploader(SqueakServerAddress serverAddress, SqueakDao squeakDao) {
        this.serverAddress = serverAddress;
        this.squeakDao = squeakDao;
        client = new SqueakRPCClient(serverAddress.getHost(), serverAddress.getPort());

    }

    public void upload(Squeak squeak) throws InterruptedException {
        // TODO: implement
        // client.postSqueak(squeak);
    }

    public void uploadSync(List<String> uploadAddresses) {
        Log.i(getClass().getName(), "Calling uploadSync...");
        try {
            List<Sha256Hash> serverHashes = client.lookupSqueaks(uploadAddresses, 0, 0);
            Log.i(getClass().getName(), "Got server hashes: " + serverHashes);
        } catch (Exception e) {
            Log.i(getClass().getName(), "Got exception : " + e);
        }

        // TODO: check against database, and upload the missing hashes
        // List<Sha256Hash> localHashes = squeakDao.fetchSqueakByAddresses(uploadAddresses);
        // For every local hash not in serverhashes, upload.
    }
}
