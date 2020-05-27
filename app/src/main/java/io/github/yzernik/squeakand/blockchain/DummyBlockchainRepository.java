package io.github.yzernik.squeakand.blockchain;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.Sha256Hash;

public class DummyBlockchainRepository implements BlockchainRepository{

    private Blockchain blockchain;

    public DummyBlockchainRepository(Application application) {
        blockchain = new DummyBlockchain();
    }

    public LiveData<BlockInfo> getLatestBlock() {
        MutableLiveData<BlockInfo> liveBlockTip = new MutableLiveData<>();
        liveBlockTip.setValue(blockchain.getLatestBlock());
        return liveBlockTip;
    }

    public LiveData<Sha256Hash> getBlockHash(int blockHeight) {
        MutableLiveData<Sha256Hash> liveBlockHash = new MutableLiveData<>();
        liveBlockHash.setValue(blockchain.getBlockHash(blockHeight));
        return liveBlockHash;
    }

}
