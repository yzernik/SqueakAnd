package io.github.yzernik.squeakand.blockchain;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.Sha256Hash;

public class DummyBlockchainRepository implements BlockchainRepository{

    private Blockchain blockchain;

    // Note that in order to unit test the TodoRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public DummyBlockchainRepository(Application application) {
        // TodoRoomDatabase db = TodoRoomDatabase.getDatabase(application);
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
