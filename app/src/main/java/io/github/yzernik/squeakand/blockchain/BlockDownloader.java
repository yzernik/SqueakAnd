package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.BitcoinSerializer;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import io.github.yzernik.electrumclient.ElectrumClient;
import io.github.yzernik.electrumclient.SubscribeHeadersClientConnection;
import io.github.yzernik.electrumclient.SubscribeHeadersResponse;
import io.github.yzernik.electrumclient.exceptions.ElectrumClientException;

import static org.bitcoinj.core.Utils.HEX;

public class BlockDownloader {

    private static final int MAX_RETRIES = 10;
    private static final int INITIAL_BACKOFF_TIME_MS = 1000;

    private final MutableLiveData<BlockInfo> liveBlockTip;
    private final MutableLiveData<ElectrumBlockchainRepository.ConnectionStatus> liveConnectionStatus;
    private final ExecutorService executorService;
    private Future<String> future = null;

    public BlockDownloader(MutableLiveData<BlockInfo> liveBlockTip, MutableLiveData<ElectrumBlockchainRepository.ConnectionStatus> liveConnectionStatus) {
        this.liveBlockTip = liveBlockTip;
        this.liveConnectionStatus = liveConnectionStatus;
        liveConnectionStatus.setValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void setElectrumServer(ElectrumServerAddress serverAddress) {
        if (future != null) {
            Log.i(getClass().getName(), "Cancelling running download task.");
            future.cancel(true);
        }

        BlockDownloadTask newDownloadTask = new BlockDownloadTask(serverAddress);
        Log.i(getClass().getName(), "Submitting new download task.");
        future = executorService.submit(newDownloadTask);
    }

    private void tryLoadLiveData(ElectrumClient electrumClient) throws ElectrumClientException, IOException {
        Log.i(getClass().getName(), "Loading live data with electrum client: " + electrumClient);
        liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.CONNECTING);
        SubscribeHeadersClientConnection connection = electrumClient.subscribeHeaders(header -> {
            Log.i(getClass().getName(), "Downloaded header: " + header);
            BlockInfo blockInfo = parseHeaderResponse(header);
            liveBlockTip.postValue(blockInfo);
        });
        SubscribeHeadersResponse response = null;
        try {
            response = connection.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
            connection.close();
            Log.i(getClass().getName(), "Closed connection with electrum client: " + electrumClient);
            liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
        }
        liveBlockTip.postValue(parseHeaderResponse(response));
        liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.CONNECTED);
        Log.i(getClass().getName(), "Sleeping download thread...");
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
            connection.close();
            Log.i(getClass().getName(), "Closed connection with electrum client: " + electrumClient);
            liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
        }
    }


    private BlockInfo parseHeaderResponse(SubscribeHeadersResponse response) {
        NetworkParameters networkParameters = io.github.yzernik.squeakand.networkparameters.NetworkParameters.getNetworkParameters();
        BitcoinSerializer bitcoinSerializer = new BitcoinSerializer(networkParameters, false);
        byte[] blockBytes = HEX.decode(response.hex);
        Block block = bitcoinSerializer.makeBlock(blockBytes);
        return new BlockInfo(block.getHash(), response.height);
    }


    class BlockDownloadTask implements Callable<String> {
        private final ElectrumServerAddress serverAddress;

        public BlockDownloadTask(ElectrumServerAddress serverAddress) {
            this.serverAddress = serverAddress;
        }

        @Override
        public String call() throws Exception {
            Log.i(getClass().getName(), "Calling call...");
            ElectrumClient electrumClient = new ElectrumClient(serverAddress.getHost(), serverAddress.getPort());
            int maxRetries = MAX_RETRIES;
            int backoff = INITIAL_BACKOFF_TIME_MS;
            int retryCounter = 0;
            while (retryCounter < maxRetries) {
                try {
                    tryLoadLiveData(electrumClient);
                } catch (Exception e) {
                    retryCounter++;
                    Log.e(getClass().getName(), "FAILED - Command failed on retry " + retryCounter + " of " + maxRetries + " error: " + e);
                    if (retryCounter >= maxRetries) {
                        Log.e(getClass().getName(), "Max retries exceeded.");
                        break;
                    }
                } finally {
                    liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
                }
                backoff *= 2;
                Thread.sleep(backoff);
            }

            return "";
        }



    }

}
