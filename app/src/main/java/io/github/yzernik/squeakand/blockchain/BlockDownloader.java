package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.BitcoinSerializer;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import io.github.yzernik.electrumclient.ElectrumClient;
import io.github.yzernik.electrumclient.SubscribeHeadersResponse;

import static org.bitcoinj.core.Utils.HEX;

public class BlockDownloader {

    private static final int MAX_RETRIES = 10;
    private static final int INITIAL_BACKOFF_TIME_MS = 1000;

    private final MutableLiveData<BlockInfo> liveBlockTip;
    private final ExecutorService executorService;
    private Future<String> future = null;

    public BlockDownloader(MutableLiveData<BlockInfo> liveBlockTip) {
        this.liveBlockTip = liveBlockTip;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void setElectrumServer(ElectrumServerAddress serverAddress) {
        if (future != null) {
            future.cancel(true);
        }

        BlockDownloadTask newDownloadTask = new BlockDownloadTask(serverAddress);
        executorService.submit(newDownloadTask);
    }

    private void tryLoadLiveData(ElectrumClient electrumClient) throws Throwable {
        Log.i(getClass().getName(), "Loading live data with electrum client: " + electrumClient);
        Stream<SubscribeHeadersResponse> headers = electrumClient.subscribeHeaders();
        headers.forEach(header -> {
            Log.i(getClass().getName(), "Downloaded header: " + header);
            BlockInfo blockInfo = parseHeaderResponse(header);
            liveBlockTip.postValue(blockInfo);
        });
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
                } catch (Throwable throwable) {
                    retryCounter++;
                    Log.e(getClass().getName(), "FAILED - Command failed on retry " + retryCounter + " of " + maxRetries + " error: " + throwable);
                    if (retryCounter >= maxRetries) {
                        Log.e(getClass().getName(), "Max retries exceeded.");
                        break;
                    }
                }
                backoff *= 2;
                Thread.sleep(backoff);
            }

            return "";
        }



    }

}
