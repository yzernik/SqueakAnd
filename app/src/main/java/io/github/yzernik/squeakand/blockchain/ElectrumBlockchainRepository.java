package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.Sha256Hash;


public class ElectrumBlockchainRepository {

    private static volatile ElectrumBlockchainRepository INSTANCE;

    private MutableLiveData<ElectrumServerAddress> liveServerAddress = new MutableLiveData<>();
    private MutableLiveData<BlockInfo> liveBlockTip = new MutableLiveData<>();
    private MutableLiveData<ServerUpdate.ConnectionStatus> liveConnectionStatus = new MutableLiveData<>();
    private MutableLiveData<ServerUpdate> liveServerUpdate = new MutableLiveData<>();
    private BlockDownloader blockDownloader;

    private ElectrumBlockchainRepository() {
        // Singleton constructor, only called by static method.
        liveServerAddress.setValue(null);
        liveBlockTip.setValue(null);
        blockDownloader = new BlockDownloader(liveBlockTip, liveConnectionStatus, liveServerUpdate);
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
        // liveServerAddress.setValue(serverAddress);

        // Set up electrum client with server config, and load livedata.
        // loadLiveData(host, port);
        blockDownloader.setElectrumServer(serverAddress);
    }

/*    public LiveData<BlockInfo> getLatestBlock() {
        Log.i(getClass().getName(), "Returning latest block tip live data from repository..");
        return liveBlockTip;
    }*/

    public LiveData<Sha256Hash> getBlockHash(int blockHeight) {
        // TODO
        return null;
    }

/*    public LiveData<ElectrumServerAddress> getServerAddress() {
        return liveServerAddress;
    }*/

/*    public LiveData<ServerUpdate.ConnectionStatus> getConnectionStatus() {
        return liveConnectionStatus;
    }*/

    public LiveData<ServerUpdate> getServerUpdate() {
        return liveServerUpdate;
    }

}
