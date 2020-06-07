package io.github.yzernik.squeakand.server;

import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.SqueakDao;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileDao;
import io.github.yzernik.squeaklib.core.Squeak;

public class SqueakNetworkController {

    private static final int DEFAULT_MIN_BLOCK = 0;
    private static final int DEFAULT_MAX_BLOCK = 1000000000;

    private final SqueakDao squeakDao;
    private final SqueakProfileDao squeakProfileDao;

    public SqueakNetworkController(SqueakDao squeakDao, SqueakProfileDao squeakProfileDao) {
        this.squeakDao = squeakDao;
        this.squeakProfileDao = squeakProfileDao;
    }

    public void publish(Squeak squeak) {
        // Publish to all connected servers
        for (SqueakServerAddress serverAddress: getServers()) {
            UploaderDownloader uploaderDownloader = new UploaderDownloader(serverAddress, squeakDao);
            uploaderDownloader.upload(squeak);
        }
    }

    public Squeak download(Sha256Hash hash) {
        // Try to download from all servers
        for (SqueakServerAddress serverAddress: getServers()) {
            UploaderDownloader uploaderDownloader = new UploaderDownloader(serverAddress, squeakDao);
            Squeak squeak = uploaderDownloader.download(hash);
            if (squeak != null) {
                return squeak;
            }
        }
        return null;
    }

    public void sync() {
        trySync();
    }

    private List<SqueakServerAddress> getServers() {
        SqueakServerAddress localServer = new SqueakServerAddress("10.0.2.2", 8774);
        return Arrays.asList(localServer);
    }

    private List<String> getUploadAddresses() {
        List<SqueakProfile> signingProfiles = squeakProfileDao.getProfilesToUpload();
        Log.i(getClass().getName(), "Got number of profiles to upload: " + signingProfiles.size());
        return signingProfiles.stream()
                .map(profile -> profile.getAddress())
                .collect(Collectors.toList());
    }

    private List<String> getDownloadAddresses() {
        List<SqueakProfile> signingProfiles = squeakProfileDao.getProfilesToDownload();
        Log.i(getClass().getName(), "Got number of profiles to download: " + signingProfiles.size());
        return signingProfiles.stream()
                .map(profile -> profile.getAddress())
                .collect(Collectors.toList());
    }

    private void trySync() {
        List<SqueakServerAddress> serverAddresses = getServers();
        List<String> uploadAddresses = getUploadAddresses();

        Log.i(getClass().getName(), "Doing another round of syncing with servers: " + serverAddresses);
        Log.i(getClass().getName(), "Doing another round of syncing with number of servers: " + serverAddresses.size());
        for (SqueakServerAddress serverAddress: serverAddresses) {
            try {
                trySyncServer(serverAddress);
            } catch (io.grpc.StatusRuntimeException e) {
                Log.e(getClass().getName(),"Failed to sync with server " + serverAddress + " with error: " + e);
            }
        }
        Log.i(getClass().getName(), "Finished round of syncing.");
    }

    private void trySyncServer(SqueakServerAddress serverAddress) {
        UploaderDownloader uploaderDownloader = new UploaderDownloader(serverAddress, squeakDao);

        // Sync uploads
        uploaderDownloader.uploadSync(getUploadAddresses(), DEFAULT_MIN_BLOCK, DEFAULT_MAX_BLOCK);

        // Sync downloads
        uploaderDownloader.downloadSync(getDownloadAddresses(), DEFAULT_MIN_BLOCK, DEFAULT_MAX_BLOCK);
    }

}
