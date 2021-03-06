package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.yzernik.electrumclient.ElectrumClient;
import io.github.yzernik.electrumclient.SubscribeHeadersResponse;

public class BlockDownloader {

    private final ElectrumConnection downloaderController;
    private final ExecutorService executorService;
    private Future<String> future = null;

    BlockDownloader(ElectrumConnection downloaderController) {
        this.downloaderController = downloaderController;
        this.executorService = Executors.newCachedThreadPool();
    }

    synchronized void reset() {
        if (future != null) {
            future.cancel(true);
        }

        ElectrumServerAddress serverAddress = downloaderController.getCurrentDownloadServer();
        // Start a new download task if the current server address is not null.
        if (serverAddress != null) {
            BlockDownloadTask newDownloadTask = new BlockDownloadTask(serverAddress);
            Log.i(getClass().getName(), "Submitting new download task.");
            future = executorService.submit(newDownloadTask);
        }
    }

    private BlockInfo parseHeaderResponse(SubscribeHeadersResponse response) {
        return BlockUtil.parseHeaderResponse(response);
    }

    class BlockDownloadTask implements Callable<String> {
        private static final int MAX_RETRIES = 5;
        private static final int INITIAL_BACKOFF_TIME_MS = 1000;

        private final ElectrumServerAddress serverAddress;
        private int retryCounter = 0;

        BlockDownloadTask(ElectrumServerAddress serverAddress) {
            this.serverAddress = serverAddress;
        }

        @Override
        public String call() {
            Log.i(getClass().getName(), "Calling call.");
            while (true) {
                try {
                    tryLoadBlockSubscriptionWithRetries();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(getClass().getName(), "CANCELLED - Command because of interrupt. error: " + e);
                    return "";
                }
            }
        }

        private void tryLoadBlockSubscriptionWithRetries() throws InterruptedException {
            InetSocketAddress address = new InetSocketAddress(serverAddress.getHost(), serverAddress.getPort());
            ElectrumClient electrumClient = new ElectrumClient(address, executorService);
            while (retryCounter < MAX_RETRIES) {
                try {
                    tryLoadBlockSubscription(electrumClient);
                } catch (ExecutionException  e) {
                    retryCounter++;
                    Log.e(getClass().getName(), "FAILED - Command failed on retry " + retryCounter + " of " + MAX_RETRIES + " error: " + e);
                }
                int backoff = INITIAL_BACKOFF_TIME_MS << retryCounter;
                Thread.sleep(backoff);
            }
            resetRetryCounter();
        }

        private void tryLoadBlockSubscription(ElectrumClient electrumClient) throws ExecutionException, InterruptedException {
            downloaderController.setStatusConnecting();
            Future<SubscribeHeadersResponse> responseFuture = electrumClient.subscribeHeaders(header -> {
                Log.i(getClass().getName(), "Downloaded header: " + header);
                BlockInfo blockInfo = parseHeaderResponse(header);
                downloaderController.setStatusConnected(blockInfo);
                resetRetryCounter();
            });
            try {
                responseFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                downloaderController.setStatusDisconnected();
                responseFuture.cancel(true);
                throw e;
            }
        }

        private void resetRetryCounter() {
            retryCounter = 0;
        }

    }

}
