package io.github.yzernik.squeakand.server;

import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SqueakNetworkAsyncClient {

    private static final int SYNC_TIMELINE_TIMEOUT_S = 30;
    private static final int DEFAULT_NUM_THREAD_ANCESTORS = 10;

    private SqueakNetworkController squeakNetworkController;
    private final ExecutorService executorService;

    public SqueakNetworkAsyncClient(SqueakNetworkController squeakNetworkController) {
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

    public void getOffers(Sha256Hash squeakHash, SqueakServerResponseHandler responseHandler) {
        GetOffersTask getOffersTask = new GetOffersTask(squeakHash, responseHandler);
        Log.i(getClass().getName(), "Submitting new get offers task.");
        executorService.submit(getOffersTask);
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
                squeakNetworkController.fetchThreadAncestors(squeakHash, DEFAULT_NUM_THREAD_ANCESTORS);
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


    class GetOffersTask implements Callable<Integer> {

        private Sha256Hash squeakHash;
        private SqueakServerResponseHandler responseHandler;

        GetOffersTask(Sha256Hash squeakHash, SqueakServerResponseHandler responseHandler) {
            this.responseHandler = responseHandler;
            this.squeakHash = squeakHash;
        }

        @Override
        public Integer call() {
            try {
                squeakNetworkController.getOffers(squeakHash);
                Log.i(getClass().getName(),"Fetched offers from servers.");
                responseHandler.onSuccess();
            } catch (Exception e) {
                Log.e(getClass().getName(),"Failed to fetch offers with error: " + e);
                responseHandler.onFailure(e);
                throw e;
            }
            return 0;
        }
    }



}
