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
    private final MutableLiveData<ElectrumBlockchainRepository.ConnectionStatus> liveConnectionStatus;
    private final ExecutorService executorService;
    private Future<String> future = null;

    BlockDownloader(MutableLiveData<BlockInfo> liveBlockTip, MutableLiveData<ElectrumBlockchainRepository.ConnectionStatus> liveConnectionStatus) {
        this.liveBlockTip = liveBlockTip;
        this.liveConnectionStatus = liveConnectionStatus;
        liveConnectionStatus.setValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
        this.executorService = Executors.newCachedThreadPool();
    }

    synchronized void setElectrumServer(ElectrumServerAddress serverAddress) {
        if (future != null) {
            future.cancel(true);
        }

        BlockDownloadTask newDownloadTask = new BlockDownloadTask(serverAddress);
        Log.i(getClass().getName(), "Submitting new download task.");
        future = executorService.submit(newDownloadTask);
    }


    private BlockInfo parseHeaderResponse(SubscribeHeadersResponse response) {
        NetworkParameters networkParameters = io.github.yzernik.squeakand.networkparameters.NetworkParameters.getNetworkParameters();
        BitcoinSerializer bitcoinSerializer = new BitcoinSerializer(networkParameters, false);
        byte[] blockBytes = HEX.decode(response.hex);
        Block block = bitcoinSerializer.makeBlock(blockBytes);
        return new BlockInfo(block.getHash(), response.height);
    }


    class BlockDownloadTask implements Callable<String> {
        private static final int MAX_RETRIES = 10;
        private static final int INITIAL_BACKOFF_TIME_MS = 1000;

        private final ElectrumServerAddress serverAddress;
        private int retryCounter = 0;

        BlockDownloadTask(ElectrumServerAddress serverAddress) {
            this.serverAddress = serverAddress;
        }

        @Override
        public String call() {
            Log.i(getClass().getName(), "Calling call.");
            // InetSocketAddress address = new InetSocketAddress(serverAddress.getHost(), serverAddress.getPort());
            InetSocketAddress address = new InetSocketAddress(serverAddress.getHost(), serverAddress.getPort());
            ElectrumClient electrumClient = new ElectrumClient(address, executorService);
            while (retryCounter < MAX_RETRIES) {
                try {
                    tryLoadLiveData(electrumClient);
                } catch (ExecutionException  e) {
                    retryCounter++;
                    Log.e(getClass().getName(), "FAILED - Command failed on retry " + retryCounter + " of " + MAX_RETRIES + " error: " + e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(getClass().getName(), "CANCELLED - Command because of interrupt. error: " + e);
                    break;
                }
                try {
                    int backoff = INITIAL_BACKOFF_TIME_MS << retryCounter;
                    Thread.sleep(backoff);
                } catch (InterruptedException e) {
                    break;
                }
            }
            return "";
        }

        private void tryLoadLiveData(ElectrumClient electrumClient) throws ExecutionException, InterruptedException {
            liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.CONNECTING);
            Future<SubscribeHeadersResponse> responseFuture = electrumClient.subscribeHeaders(header -> {
                Log.i(getClass().getName(), "Downloaded header: " + header);
                BlockInfo blockInfo = parseHeaderResponse(header);
                liveBlockTip.postValue(blockInfo);
                liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.CONNECTED);
                resetRetryCounter();
            });
            try {
                responseFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
                responseFuture.cancel(true);
                throw e;
            }
        }

        private void resetRetryCounter() {
            retryCounter = 0;
        }

    }

}
