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

    private final MutableLiveData<BlockInfo> liveBlockTip;
    private final ExecutorService executorService;
    private Future<String> future = null;

    public BlockDownloader(MutableLiveData<BlockInfo> liveBlockTip) {
        this.liveBlockTip = liveBlockTip;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void setElectrumServer(String host, int port) {
        if (future != null) {
            future.cancel(true);
        }

        BlockDownloadTask newDownloadTask = new BlockDownloadTask(host, port);
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
        private final String host;
        private final int port;

        public BlockDownloadTask(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public String call() throws Exception {

            Log.i(getClass().getName(), "Calling call...");
            ElectrumClient electrumClient = new ElectrumClient(host, port);
            int maxRetries = 5;
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
            }

            return "";
        }



    }

}
