package io.github.yzernik.squeakand.server;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerUploader {

    private final SqueakNetworkController squeakNetworkController;
    private final ExecutorService executorService;
    private Future<String> future = null;

    public ServerUploader(SqueakNetworkController squeakNetworkController) {
        this.squeakNetworkController = squeakNetworkController;
        this.executorService =  Executors.newFixedThreadPool(10);
    }

    public synchronized void startUploadTask() {
        if (future != null) {
            future.cancel(true);
        }

        UploadTask newUploadTask = new UploadTask();
        Log.i(getClass().getName(), "Submitting new server upload task.");
        future = executorService.submit(newUploadTask);
    }


    class UploadTask implements Callable<String> {

        UploadTask() {
        }

        @Override
        public String call() throws InterruptedException {
            Log.i(getClass().getName(), "Calling call.");
            squeakNetworkController.publishAllEnqueued();
            return "";
        }

    }
}
