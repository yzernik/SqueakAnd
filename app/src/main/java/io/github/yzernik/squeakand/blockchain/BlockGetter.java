package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import org.bitcoinj.core.Block;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.yzernik.electrumclient.ElectrumClient;
import io.github.yzernik.electrumclient.GetHeaderResponse;

public class BlockGetter {

    private final ElectrumDownloaderController downloaderController;
    private final ExecutorService executorService;
    private Future<String> future = null;

    BlockGetter(ElectrumDownloaderController downloaderController) {
        this.downloaderController = downloaderController;
        this.executorService = Executors.newCachedThreadPool();
    }

    private Future<GetHeaderResponse> getHeaderResponseFuture(int height) throws ExecutionException, InterruptedException {
        ElectrumServerAddress serverAddress = downloaderController.getCurrentDownloadServer();
        InetSocketAddress address = new InetSocketAddress(serverAddress.getHost(), serverAddress.getPort());
        ElectrumClient electrumClient = new ElectrumClient(address, executorService);
        return electrumClient.getHeader(height);
    }

    private Block parseBlockHeader(GetHeaderResponse response) {
        return BlockUtil.parseGetHeaderResponse(response);
    }

    public Future<Block> getBlockHeader(int height) {
        GetBlockHeaderTask task = new GetBlockHeaderTask(height);
        Future<Block> blockFuture = executorService.submit(task);
        return blockFuture;
    }


    class GetBlockHeaderTask implements Callable<Block> {
        private static final int MAX_RETRIES = 5;
        private static final int INITIAL_BACKOFF_TIME_MS = 1000;

        private final int height;

        GetBlockHeaderTask(int height) {
            this.height = height;
        }

        @Override
        public Block call() {
            Log.i(getClass().getName(), "Calling call.");
            try {
                Future<GetHeaderResponse> responseFuture = getHeaderResponseFuture(height);
                GetHeaderResponse response = responseFuture.get();
                Block block = parseBlockHeader(response);
                return block;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Log.e(getClass().getName(), "CANCELLED - Command because of interrupt. error: " + e);
                return null;
            }
        }

    }


}
