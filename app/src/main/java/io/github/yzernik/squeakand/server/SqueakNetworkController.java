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
import io.github.yzernik.squeakand.blockchain.ElectrumConnection;
import io.github.yzernik.squeakand.blockchain.ServerUpdate;
import io.github.yzernik.squeakand.blockchain.status.ElectrumDownloaderStatus;
import io.github.yzernik.squeakand.squeaks.SqueaksController;
import io.github.yzernik.squeaklib.core.Squeak;

public class SqueakNetworkController {

    private static final int DEFAULT_MIN_BLOCK = 0;
    private static final int DEFAULT_MAX_BLOCK = Integer.MAX_VALUE;
    private static final int DEFAULT_BLOCK_RANGE = 1008; // 1 week in blocks.

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
        for (SqueakServer server: getServers()) {
            SqueakServerController squeakServerController = new SqueakServerController(server, squeaksController);
            try {
                squeakServerController.upload(squeak);
            } catch (io.grpc.StatusRuntimeException e) {
                Log.e(getClass().getName(),"Failed to upload to server " + server.serverAddress + " with error: " + e);
            }
        }
    }

    public Squeak fetch(Sha256Hash hash) {
        // Try to download from all servers if the squeak is not already in the local database
        SqueakEntryWithProfile squeakEntryWithProfile = squeaksController.fetchSqueakWithProfileByHash(hash);
        if (squeakEntryWithProfile != null) {
            return squeakEntryWithProfile.squeakEntry.getSqueak();
        }

        for (SqueakServer server: getServers()) {
            SqueakServerController squeakServerController = new SqueakServerController(server, squeaksController);
            Squeak squeak = null;
            try {
                return squeakServerController.download(hash);
            } catch (io.grpc.StatusRuntimeException e) {
                Log.e(getClass().getName(),"Failed to download " + hash + " from server " + server + " with error: " + e);
            }
        }
        return null;
    }

    public void getOffers(Sha256Hash squeakHash) {
        // Try to get offers from all servers
        for (SqueakServer server: getServers()) {
            SqueakServerController squeakServerController = new SqueakServerController(server, squeaksController);
            try {
                squeakServerController.getOffer(squeakHash);
            } catch (io.grpc.StatusRuntimeException e) {
                Log.e(getClass().getName(),"Failed to get offer for hash " + squeakHash + " from server " + server + " with error: " + e);
            }
        }
    }

    public int sync(ElectrumConnection electrumConnection) throws Exception {
        ElectrumDownloaderStatus electrumDownloaderStatus = electrumConnection.getCurrentStatusUpdate();
        if (!electrumDownloaderStatus.getConnectionStatus().equals(ServerUpdate.ConnectionStatus.CONNECTED)) {
            Log.i(getClass().getName(), "Unable to sync because electrum is not connected.");
            throw new Exception("Unable to sync because electrum is not connected.");
        }

        int currentBlockHeight = electrumDownloaderStatus.getLatestBlockInfo().getHeight();
        trySync(currentBlockHeight);
        // TODO: return the number of squeaks downloaded/uploaded.
        return 0;
    }

    private List<SqueakServer> getServers() {
        /*
        SqueakServerAddress localServer = new SqueakServerAddress("10.0.2.2", 8774);
        return Arrays.asList(localServer);*/
        return squeakServerDao.getServers();
    }

    private List<String> getUploadAddresses() {
        List<SqueakProfile> signingProfiles = squeakProfileDao.getProfilesToUpload();
        return signingProfiles.stream()
                .map(profile -> profile.getAddress())
                .collect(Collectors.toList());
    }

    private List<String> getDownloadAddresses() {
        List<SqueakProfile> signingProfiles = squeakProfileDao.getProfilesToDownload();
        return signingProfiles.stream()
                .map(profile -> profile.getAddress())
                .collect(Collectors.toList());
    }

    private void trySync(int currentBlockHeight) {
        List<SqueakServer> servers = getServers();
        for (SqueakServer server: servers) {
            Log.i(getClass().getName(), "Syncing with server: " + server.serverAddress);
            try {
                trySyncServer(server, currentBlockHeight);
            } catch (io.grpc.StatusRuntimeException e) {
                Log.e(getClass().getName(),"Failed to sync with server " + server + " with error: " + e);
            } catch (Exception e){
                Log.e(getClass().getName(),"Failed to sync with server " + server + " with error: " + e);
            }
        }
    }

    private void trySyncServer(SqueakServer server, int currentBlockHeight) {
        SqueakServerController squeakServerController = new SqueakServerController(server, squeaksController);

        // Get the block range to sync with the server.
        int minBlock = currentBlockHeight - DEFAULT_BLOCK_RANGE;
        int maxBlock = DEFAULT_MAX_BLOCK;

        // Sync downloads
        squeakServerController.downloadSync(getDownloadAddresses(), minBlock, maxBlock);

        // Sync uploads
        squeakServerController.uploadSync(getUploadAddresses(), minBlock, maxBlock);
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
            Squeak replyTo = fetch(currentReplyTo);
            if (replyTo == null) {
                // Finish because fetch squeak failed
                return;
            }

            currentReplyTo = replyTo.getHashReplySqk();
            numDownloaded++;

            // TODO: use the isReply method of squeak when it is available.
            if(currentReplyTo.equals(Sha256Hash.ZERO_HASH)) {
                // Finish because current squeak is not a reply.
                return;
            }
        }

    }

}
