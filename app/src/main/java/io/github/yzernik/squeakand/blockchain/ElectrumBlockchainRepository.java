package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.Sha256Hash;


public class ElectrumBlockchainRepository implements BlockchainRepository {

    private static volatile ElectrumBlockchainRepository INSTANCE;

    private MutableLiveData<ElectrumServerAddress> liveServerAddress = new MutableLiveData<>();
    private MutableLiveData<BlockInfo> liveBlockTip = new MutableLiveData<>();
    private MutableLiveData<ElectrumError> error = new MutableLiveData<>();
    private BlockDownloader blockDownloader;

    private ElectrumBlockchainRepository() {
        // Singleton constructor, only called by static method.
        liveServerAddress.setValue(null);
        liveBlockTip.setValue(null);
        blockDownloader = new BlockDownloader(liveBlockTip);
    }

    public static ElectrumBlockchainRepository getRepository() {
        if (INSTANCE == null) {
            synchronized (ElectrumBlockchainRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ElectrumBlockchainRepository();
                }
            }
        }
        return INSTANCE;
    }

    public void setServer(ElectrumServerAddress serverAddress) {
        liveServerAddress.setValue(serverAddress);

        // Set up electrum client with server config, and load livedata.
        // loadLiveData(host, port);
        blockDownloader.setElectrumServer(serverAddress);
    }


    /*    private void loadLiveData(String host, int port) {
        Log.i(getClass().getName(), "Calling loadLiveData...");
        // do async operation to fetch blocks so the UI thread does not get blocked.
        service =  Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(), "Calling run...");
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
            }
        });
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
    }*/

    @Override
    public LiveData<BlockInfo> getLatestBlock() {
        Log.i(getClass().getName(), "Returning latest block tip live data from repository..");
        return liveBlockTip;
    }

    @Override
    public LiveData<Sha256Hash> getBlockHash(int blockHeight) {
        // TODO
        return null;
    }

    public LiveData<ElectrumServerAddress> getServerAddress() {
        return liveServerAddress;
    }

    public static class ElectrumError {

        private Throwable exception;

        public ElectrumError(Throwable exception) {
            this.exception = exception;
        }

        public Throwable getException() {
            return exception;
        }

        @NonNull
        @Override
        public String toString() {
            return exception.toString();
        }
    }

}
