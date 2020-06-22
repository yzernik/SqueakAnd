package io.github.yzernik.squeakand.server;

import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

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

    public SqueakServerAsyncClient(SqueakNetworkController squeakNetworkController) {
        this.squeakNetworkController = squeakNetworkController;
        this.executorService =  Executors.newCachedThreadPool();
    }

    public void syncTimeline(SqueakServerResponseHandler responseHandler) {
        SyncTimelineTask syncTimelineTask = new SyncTimelineTask(responseHandler);
        Log.i(getClass().getName(), "Submitting new sync timeline task.");
        executorService.submit(syncTimelineTask);
    }

    public void fetchThreadAncestors(Sha256Hash squeakHash, SqueakServerResponseHandler responseHandler) {
        FetchThreadAncestorsTask fetchThreadAncestorsTask = new FetchThreadAncestorsTask(squeakHash, responseHandler);
        Log.i(getClass().getName(), "Submitting new fetch thread ancestors task.");
        executorService.submit(fetchThreadAncestorsTask);
    }


    public interface SqueakServerResponseHandler {
        public void onSuccess();
        public void onFailure(Throwable e);
    }


    class SyncTimelineTask implements Callable<Integer> {

        private SqueakServerResponseHandler responseHandler;

        SyncTimelineTask(SqueakServerResponseHandler responseHandler) {
            this.responseHandler = responseHandler;
        }

        @Override
        public Integer call() {
            Log.i(getClass().getName(), "Calling call.");
            try {
                squeakNetworkController.sync();
                responseHandler.onSuccess();
            } catch (Exception e) {
                Log.e(getClass().getName(),"Failed to sync with servers with error: " + e);
                responseHandler.onFailure(e);
                throw e;
            }
            return 0;
        }
    }

    class FetchThreadAncestorsTask implements Callable<Integer> {

        private Sha256Hash squeakHash;
        private SqueakServerResponseHandler responseHandler;

        FetchThreadAncestorsTask(Sha256Hash squeakHash, SqueakServerResponseHandler responseHandler) {
            this.responseHandler = responseHandler;
            this.squeakHash = squeakHash;
        }

        @Override
        public Integer call() {
            try {
                squeakNetworkController.fetchThreadAncestors(squeakHash);
                Log.i(getClass().getName(),"Fetched thread ancestors.");
                responseHandler.onSuccess();
            } catch (Exception e) {
                Log.e(getClass().getName(),"Failed to fetch thread ancestors with error: " + e);
                responseHandler.onFailure(e);
                throw e;
            }
            return 0;
        }
    }

}
