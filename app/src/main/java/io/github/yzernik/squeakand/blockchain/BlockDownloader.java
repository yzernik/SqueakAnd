package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.BitcoinSerializer;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.yzernik.electrumclient.ElectrumClient;
import io.github.yzernik.electrumclient.SubscribeHeadersResponse;

import static org.bitcoinj.core.Utils.HEX;

public class BlockDownloader {

    private final MutableLiveData<BlockInfo> liveBlockTip;
    private final MutableLiveData<ServerUpdate.ConnectionStatus> liveConnectionStatus;
    private final MutableLiveData<ServerUpdate> liveServerUpdate;
    private final ExecutorService executorService;
    private Future<String> future = null;

    BlockDownloader(MutableLiveData<BlockInfo> liveBlockTip, MutableLiveData<ServerUpdate.ConnectionStatus> liveConnectionStatus, MutableLiveData<ServerUpdate> liveServerUpdate) {
        this.liveBlockTip = liveBlockTip;
        this.liveConnectionStatus = liveConnectionStatus;
        this.liveServerUpdate = liveServerUpdate;
        liveConnectionStatus.setValue(ServerUpdate.ConnectionStatus.DISCONNECTED);
        this.executorService = Executors.newCachedThreadPool();
        initialize();
    }

    synchronized void initialize() {
        setStatusDisconnected(null);
    }

    synchronized void setElectrumServer(ElectrumServerAddress serverAddress) {
        if (future != null) {
            future.cancel(true);
        }

        BlockDownloadTask newDownloadTask = new BlockDownloadTask(serverAddress);
        Log.i(getClass().getName(), "Submitting new download task.");
        future = executorService.submit(newDownloadTask);
    }


    void setStatusConnected(ElectrumServerAddress serverAddress, BlockInfo blockInfo) {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.CONNECTED,
                serverAddress,
                blockInfo
        );
        liveServerUpdate.postValue(serverUpdate);
    }

    void setStatusDisconnected(ElectrumServerAddress serverAddress) {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.DISCONNECTED,
                serverAddress,
                null
        );
        liveServerUpdate.postValue(serverUpdate);
    }

    void setStatusConnecting(ElectrumServerAddress serverAddress) {
        ServerUpdate serverUpdate = new ServerUpdate(
                ServerUpdate.ConnectionStatus.CONNECTING,
                serverAddress,
                null
        );
        liveServerUpdate.postValue(serverUpdate);
    }

    private BlockInfo parseHeaderResponse(SubscribeHeadersResponse response) {
        NetworkParameters networkParameters = io.github.yzernik.squeakand.networkparameters.NetworkParameters.getNetworkParameters();
        BitcoinSerializer bitcoinSerializer = new BitcoinSerializer(networkParameters, false);
        byte[] blockBytes = HEX.decode(response.hex);
        Block block = bitcoinSerializer.makeBlock(blockBytes);
        return new BlockInfo(block.getHash(), response.height);
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
                    tryLoadLiveDataWithRetries();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(getClass().getName(), "CANCELLED - Command because of interrupt. error: " + e);
                    return "";
                }
            }
        }

        private void tryLoadLiveDataWithRetries() throws InterruptedException {
            InetSocketAddress address = new InetSocketAddress(serverAddress.getHost(), serverAddress.getPort());
            ElectrumClient electrumClient = new ElectrumClient(address, executorService);
            while (retryCounter < MAX_RETRIES) {
                try {
                    tryLoadLiveData(electrumClient);
                } catch (ExecutionException  e) {
                    retryCounter++;
                    Log.e(getClass().getName(), "FAILED - Command failed on retry " + retryCounter + " of " + MAX_RETRIES + " error: " + e);
                }
                int backoff = INITIAL_BACKOFF_TIME_MS << retryCounter;
                Thread.sleep(backoff);
            }
            resetRetryCounter();
        }

        private void tryLoadLiveData(ElectrumClient electrumClient) throws ExecutionException, InterruptedException {
            liveConnectionStatus.postValue(ServerUpdate.ConnectionStatus.CONNECTING);
            updateStatusConnecting();
            Future<SubscribeHeadersResponse> responseFuture = electrumClient.subscribeHeaders(header -> {
                Log.i(getClass().getName(), "Downloaded header: " + header);
                BlockInfo blockInfo = parseHeaderResponse(header);
                liveBlockTip.postValue(blockInfo);
                liveConnectionStatus.postValue(ServerUpdate.ConnectionStatus.CONNECTED);
                updateStatusConnected(blockInfo);
                resetRetryCounter();
            });
            try {
                responseFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                liveConnectionStatus.postValue(ServerUpdate.ConnectionStatus.DISCONNECTED);
                updateStatusDisconnected();
                responseFuture.cancel(true);
                throw e;
            }
        }

        private void resetRetryCounter() {
            retryCounter = 0;
        }

        private void updateStatusConnected(BlockInfo blockInfo) {
            setStatusConnected(serverAddress, blockInfo);
        }

        private void updateStatusDisconnected() {
            setStatusDisconnected(serverAddress);
        }

        private void updateStatusConnecting() {
            setStatusConnecting(serverAddress);
        }

    }

}
