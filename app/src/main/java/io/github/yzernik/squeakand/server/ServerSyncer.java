package io.github.yzernik.squeakand.server;

import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.yzernik.squeakand.SqueakDao;

public class ServerSyncer {

    private static final int DEFAULT_MIN_BLOCK = 0;
    private static final int DEFAULT_MAX_BLOCK = 1000000000;

    private final SqueakDao squeakDao;
    private final ExecutorService executorService;
    private Future<String> future = null;
    private List<SqueakServerAddress> serverAddresses;
    private List<String> uploadAddresses;

    public ServerSyncer(SqueakDao squeakDao) {
        this.squeakDao = squeakDao;
        this.executorService =  Executors.newFixedThreadPool(10);

        serverAddresses = Collections.emptyList();
        uploadAddresses = Collections.emptyList();
    }

    public synchronized void startSyncTask() {
        if (future != null) {
            future.cancel(true);
        }

        SyncTask newSyncTask = new SyncTask(serverAddresses, uploadAddresses);
        Log.i(getClass().getName(), "Submitting new server sync task.");
        future = executorService.submit(newSyncTask);
    }

    public void setServers(List<SqueakServerAddress> serverAddresses) {
        // Restart task with the given servers
        this.serverAddresses = serverAddresses;
        startSyncTask();
    }

    public void setUploadAddresses(List<String> uploadAddresses) {
        // Restart task with the given upload addresses
        this.uploadAddresses = uploadAddresses;
        startSyncTask();
    }


    class SyncTask implements Callable<String> {
        private final List<SqueakServerAddress> serverAddresses;
        private final List<String> uploadAddresses;

        SyncTask(List<SqueakServerAddress> serverAddresses, List<String> uploadAddresses) {
            this.serverAddresses = serverAddresses;
            this.uploadAddresses = uploadAddresses;
        }

        @Override
        public String call() throws InterruptedException {
            Log.i(getClass().getName(), "Calling call.");
            while (true) {
                Log.i(getClass().getName(), "Doing another round of syncing with servers: " + serverAddresses);
                Log.i(getClass().getName(), "Doing another round of syncing with number of servers: " + serverAddresses.size());
                for (SqueakServerAddress serverAddress: serverAddresses) {
                    // Upload
                    Uploader uploader = new Uploader(serverAddress, squeakDao);
                    uploader.uploadSync(uploadAddresses, DEFAULT_MIN_BLOCK, DEFAULT_MAX_BLOCK);

                    // TODO: Download
                }
                Log.i(getClass().getName(), "Finished round of syncing.");

                // Sleep until the next sync
                Thread.sleep(60000);
            }
        }

    }



}
