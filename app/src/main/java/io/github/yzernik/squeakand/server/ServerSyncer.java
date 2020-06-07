package io.github.yzernik.squeakand.server;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerSyncer {

    private static final int DEFAULT_SYNC_SLEEP_INTERVAL_MS = 60000;

    private final SqueakNetworkController squeakNetworkController;
    private final ExecutorService executorService;
    private Future<String> future = null;

    public ServerSyncer(SqueakNetworkController squeakNetworkController) {
        this.squeakNetworkController = squeakNetworkController;
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


    class SyncTask implements Callable<String> {

        SyncTask() {
        }

        @Override
        public String call() throws InterruptedException {
            Log.i(getClass().getName(), "Calling call.");
            while (true) {
                try {
                    squeakNetworkController.sync();
                } catch (Exception e) {
                    Log.e(getClass().getName(),"Failed to sync with servers with error: " + e);
                }
                // Sleep until the next sync
                Thread.sleep(DEFAULT_SYNC_SLEEP_INTERVAL_MS);
            }
        }

    }



}
