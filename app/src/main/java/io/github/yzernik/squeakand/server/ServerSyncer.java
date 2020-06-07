package io.github.yzernik.squeakand.server;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.SqueakDao;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakProfileDao;

public class ServerSyncer {

    private static final int DEFAULT_MIN_BLOCK = 0;
    private static final int DEFAULT_MAX_BLOCK = 1000000000;
    private static final int DEFAULT_SYNC_SLEEP_INTERVAL_MS = 60000;


    private final SqueakDao squeakDao;
    private final SqueakProfileDao squeakProfileDao;
    private final ExecutorService executorService;
    private Future<String> future = null;

    public ServerSyncer(SqueakDao squeakDao, SqueakProfileDao squeakProfileDao) {
        this.squeakDao = squeakDao;
        this.squeakProfileDao = squeakProfileDao;
        this.executorService =  Executors.newFixedThreadPool(10);
    }

    public synchronized void startSyncTask() {
        if (future != null) {
            future.cancel(true);
        }

        SyncTask newSyncTask = new SyncTask();
        Log.i(getClass().getName(), "Submitting new server sync task.");
        future = executorService.submit(newSyncTask);
    }

    private List<SqueakServerAddress> getServers() {
        SqueakServerAddress localServer = new SqueakServerAddress("10.0.2.2", 8774);
        return Arrays.asList(localServer);
    }

    private List<String> getUploadAddresses() {
        List<SqueakProfile> signingProfiles = squeakProfileDao.getProfilesToUpload();
        Log.i(getClass().getName(), "Got number of signing profiles: " + signingProfiles.size());
        Log.i(getClass().getName(), "Got signing profiles: " + signingProfiles);
        for (SqueakProfile profile: signingProfiles) {
            Log.i(getClass().getName(), "Got signing profile: " + profile);
        }

        return signingProfiles.stream()
                .map(profile -> profile.getAddress())
                .collect(Collectors.toList());
    }


    class SyncTask implements Callable<String> {

        SyncTask() {
        }

        @Override
        public String call() throws InterruptedException {
            Log.i(getClass().getName(), "Calling call.");
            while (true) {
                try {
                    trySync();
                } catch (Exception e) {
                    Log.e(getClass().getName(),"Failed to sync with servers with error: " + e);
                }
                // Sleep until the next sync
                Thread.sleep(DEFAULT_SYNC_SLEEP_INTERVAL_MS);
            }
        }

        private void trySync() {
            List<SqueakServerAddress> serverAddresses = getServers();
            List<String> uploadAddresses = getUploadAddresses();

            Log.i(getClass().getName(), "Doing another round of syncing with servers: " + serverAddresses);
            Log.i(getClass().getName(), "Doing another round of syncing with number of servers: " + serverAddresses.size());
            for (SqueakServerAddress serverAddress: serverAddresses) {
                try {
                    trySyncServer(serverAddress, uploadAddresses);
                } catch (io.grpc.StatusRuntimeException e) {
                    Log.e(getClass().getName(),"Failed to sync with server " + serverAddress + " with error: " + e);
                }
            }
            Log.i(getClass().getName(), "Finished round of syncing.");
        }

        private void trySyncServer(SqueakServerAddress serverAddress, List<String> uploadAddresses) {
            // Upload
            Uploader uploader = new Uploader(serverAddress, squeakDao);
            uploader.uploadSync(uploadAddresses, DEFAULT_MIN_BLOCK, DEFAULT_MAX_BLOCK);

            // TODO: Download
        }

    }



}
