package io.github.yzernik.squeakand.server;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SqueakServerAsyncClient {

    private static final int SYNC_TIMELINE_TIMEOUT_S = 30;

    private SqueakNetworkController squeakNetworkController;
    private final ExecutorService executorService;
    private Future<Integer> future = null;

    public SqueakServerAsyncClient(SqueakNetworkController squeakNetworkController) {
        this.squeakNetworkController = squeakNetworkController;
        this.executorService =  Executors.newFixedThreadPool(10);
    }

    public void syncTimeline(SqueakServerResponseHandler responseHandler) {
        // TODO: start the sync task, and handle the result using the response handler.
        if (future != null) {
            future.cancel(true);
        }

        SyncTimelineTask syncTimelineTask = new SyncTimelineTask();
        Log.i(getClass().getName(), "Submitting new server sync task.");
        future = executorService.submit(syncTimelineTask);

        try {
            int result = future.get(SYNC_TIMELINE_TIMEOUT_S, TimeUnit.SECONDS);
            responseHandler.onSuccess();
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            responseHandler.onFailure(e);
        }

    }


    public interface SqueakServerResponseHandler {
        public void onSuccess();
        public void onFailure(Throwable e);
    }


    class SyncTimelineTask implements Callable<Integer> {

        SyncTimelineTask() {
        }

        @Override
        public Integer call() {
            Log.i(getClass().getName(), "Calling call.");
            try {
                squeakNetworkController.sync();
            } catch (Exception e) {
                Log.e(getClass().getName(),"Failed to sync with servers with error: " + e);
                throw e;
            }
            return 0;
        }
    }

}
