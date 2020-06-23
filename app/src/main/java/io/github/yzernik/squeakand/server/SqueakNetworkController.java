package io.github.yzernik.squeakand.server;

import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileDao;
import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.SqueakServerDao;
import io.github.yzernik.squeakand.squeaks.SqueaksController;
import io.github.yzernik.squeaklib.core.Squeak;

public class SqueakNetworkController {

    private static final int DEFAULT_MIN_BLOCK = 0;
    private static final int DEFAULT_MAX_BLOCK = 1000000000;

    // private final SqueakDao squeakDao;
    private final SqueaksController squeaksController;
    private final SqueakProfileDao squeakProfileDao;
    private final SqueakServerDao squeakServerDao;
    private UploadQueue uploadQueue;

    public SqueakNetworkController(SqueaksController squeaksController, SqueakProfileDao squeakProfileDao, SqueakServerDao squeakServerDao) {
        this.squeaksController = squeaksController;
        this.squeakProfileDao = squeakProfileDao;
        this.squeakServerDao = squeakServerDao;

        // Create the upload queue
        uploadQueue = new UploadQueue();
    }

    public void enqueueToPublish(Squeak squeak) {
        Log.i(getClass().getName(), "Added squeak to queue: " + squeak.getHash());
        uploadQueue.addSqueakToUpload(squeak);
    }

    /**
     * This method runs until interrupted.
     */
    public void publishAllEnqueued() throws InterruptedException {
        while (true) {
            Squeak squeakToUpload = uploadQueue.getNextSqueakToUpload();
            Log.i(getClass().getName(), "Got squeak from queue to publish: " + squeakToUpload.getHash());
            publish(squeakToUpload);
        }
    }

    public void publish(Squeak squeak) {
        // Publish to all connected servers
        for (SqueakServerAddress serverAddress: getServers()) {
            UploaderDownloader uploaderDownloader = new UploaderDownloader(serverAddress, squeaksController);
            try {
                uploaderDownloader.upload(squeak);
            } catch (io.grpc.StatusRuntimeException e) {
                Log.e(getClass().getName(),"Failed to upload to server " + serverAddress + " with error: " + e);
            }
        }
    }

    public Squeak fetch(Sha256Hash hash) {
        // Try to download from all servers if the squeak is not already in the local database
        SqueakEntryWithProfile squeakEntryWithProfile = squeaksController.fetchSqueakWithProfileByHash(hash);
        if (squeakEntryWithProfile != null) {
            return squeakEntryWithProfile.squeakEntry.getSqueak();
        }

        for (SqueakServerAddress serverAddress: getServers()) {
            UploaderDownloader uploaderDownloader = new UploaderDownloader(serverAddress, squeaksController);
            Squeak squeak = null;
            try {
                return uploaderDownloader.download(hash);
            } catch (io.grpc.StatusRuntimeException e) {
                Log.e(getClass().getName(),"Failed to download " + hash + " from server " + serverAddress + " with error: " + e);
            }
        }
        return null;
    }

    public void getOffers(Sha256Hash squeakHash) {
        // Try to get offers from all servers
        for (SqueakServerAddress serverAddress: getServers()) {
            UploaderDownloader uploaderDownloader = new UploaderDownloader(serverAddress, squeaksController);
            try {
                uploaderDownloader.getOffer(squeakHash);
            } catch (io.grpc.StatusRuntimeException e) {
                Log.e(getClass().getName(),"Failed to get offer for hash " + squeakHash + " from server " + serverAddress + " with error: " + e);
            }
        }
    }

    public void sync() {
        trySync();
    }

    private List<SqueakServerAddress> getServers() {
        /*
        SqueakServerAddress localServer = new SqueakServerAddress("10.0.2.2", 8774);
        return Arrays.asList(localServer);*/

        List<SqueakServer> servers = squeakServerDao.getServers();
        return servers.stream()
                .map(server -> server.serverAddress)
                .collect(Collectors.toList());
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
        UploaderDownloader uploaderDownloader = new UploaderDownloader(serverAddress, squeaksController);

        // Sync downloads
        uploaderDownloader.downloadSync(getDownloadAddresses(), DEFAULT_MIN_BLOCK, DEFAULT_MAX_BLOCK);

        // Sync uploads
        uploaderDownloader.uploadSync(getUploadAddresses(), DEFAULT_MIN_BLOCK, DEFAULT_MAX_BLOCK);
    }

    /**
     * Fetch squeaks in the thread going backwards until {@code numAncestors}
     * @param squeakHash
     * @param numAncestors
     */
    public void fetchThreadAncestors(Sha256Hash squeakHash, int numAncestors) {
        int numDownloaded = 0;
        Sha256Hash currentReplyTo = squeakHash;
        while (numDownloaded < numAncestors) {
            Log.i(getClass().getName(), "Fetching ancestor squeak hash: " + currentReplyTo);
            Squeak replyTo = fetch(currentReplyTo);
            if (replyTo == null) {
                // Finish because fetch squeak failed
                return;
            }

            Log.i(getClass().getName(), "Got ancestor squeak with hash: " + replyTo.getHash());
            currentReplyTo = replyTo.getHashReplySqk();
            numDownloaded++;
            Log.i(getClass().getName(), "Number of ancestors fetched: " + numDownloaded);
            Log.i(getClass().getName(), "New currentReplyTo: " + currentReplyTo);

            // TODO: use the isReply method of squeak when it is available.
            if(currentReplyTo.equals(Sha256Hash.ZERO_HASH)) {
                // Finish because current squeak is not a reply.
                return;
            }
        }

    }

}
