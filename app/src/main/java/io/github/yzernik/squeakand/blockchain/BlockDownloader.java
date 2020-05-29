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

    BlockDownloader(MutableLiveData<BlockInfo> liveBlockTip, MutableLiveData<ElectrumBlockchainRepository.ConnectionStatus> liveConnectionStatus) {
        this.liveBlockTip = liveBlockTip;
        this.liveConnectionStatus = liveConnectionStatus;
        liveConnectionStatus.setValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
        this.executorService = Executors.newFixedThreadPool(10);
    }

    synchronized void setElectrumServer(ElectrumServerAddress serverAddress) {
        if (future != null) {
            Log.i(getClass().getName(), "Cancelling running download task.");
            future.cancel(true);
        }

        BlockDownloadTask newDownloadTask = new BlockDownloadTask(serverAddress);
        Log.i(getClass().getName(), "Submitting new download task.");
        future = executorService.submit(newDownloadTask);
    }

    private void tryLoadLiveData(ElectrumClient electrumClient) throws InterruptedException, ElectrumClientException {
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
        } catch (ElectrumClientException | InterruptedException e) {
            e.printStackTrace();
            liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
            throw e;
        }
        liveBlockTip.postValue(parseHeaderResponse(response));
        liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.CONNECTED);

        // Listen for notifications
        try {
            listenNotifications(connection);
        } catch (InterruptedException e) {
            e.printStackTrace();
            liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
            try {
                connection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            throw e;
        }

            /*
            } catch (ElectrumClientException | InterruptedException e) {
            Log.i(getClass().getName(), "Caught exception in tryLoadLiveData: " + e);
            liveConnectionStatus.postValue(ElectrumBlockchainRepository.ConnectionStatus.DISCONNECTED);
            if (connection != null) {
                Log.i(getClass().getName(), "Trying to close connection.");
                try {
                    connection.close();
                    Log.i(getClass().getName(), "Closed connection with electrum client: " + electrumClient);
                } catch (IOException ex) {
                    Log.i(getClass().getName(), "Failed to close connection");
                    ex.printStackTrace();
                }
            }
            throw e;
        }*/
    }

    private void listenNotifications(SubscribeHeadersClientConnection connection) throws InterruptedException {
        Log.i(getClass().getName(), "Sleeping download thread...");
        Thread.sleep(Integer.MAX_VALUE);
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

        BlockDownloadTask(ElectrumServerAddress serverAddress) {
            this.serverAddress = serverAddress;
        }

        @Override
        public String call() {

            Log.i(getClass().getName(), "Calling call...");
            ElectrumClient electrumClient = new ElectrumClient(serverAddress.getHost(), serverAddress.getPort());
            int backoff = INITIAL_BACKOFF_TIME_MS;
            int retryCounter = 0;
            while (retryCounter < MAX_RETRIES) {
                try {
                    Log.i(getClass().getName(), "Calling tryLoadLiveData...");
                    tryLoadLiveData(electrumClient);
                } catch (ElectrumClientException e) {
                    Log.i(getClass().getName(),"Caught ElectrumClientException in call: " + e);
                    retryCounter++;
                    Log.e(getClass().getName(), "FAILED - Command failed on retry " + retryCounter + " of " + MAX_RETRIES + " error: " + e);
                    if (retryCounter >= MAX_RETRIES) {
                        Log.e(getClass().getName(), "Max retries exceeded.");
                        break;
                    }
                } catch (InterruptedException e) {
                    Log.i(getClass().getName(),"Caught ElectrumClientException in call: " + e);
                    return "";
                }
                backoff *= 2;
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e) {
                    return "";
                }
            }

            return "";
        }



    }

}
