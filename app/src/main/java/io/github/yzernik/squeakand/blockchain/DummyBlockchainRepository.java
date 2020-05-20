package io.github.yzernik.squeakand.blockchain;

import android.app.Application;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.Todo;

public class DummyBlockchainRepository implements BlockchainRepository{

    private Blockchain blockchain;
    private LiveData<List<Todo>> mAllTodos;

    // Note that in order to unit test the TodoRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public DummyBlockchainRepository(Application application) {
        // TodoRoomDatabase db = TodoRoomDatabase.getDatabase(application);
        blockchain = new DummyBlockchain();
    }

    public LiveData<Pair<Sha256Hash, Integer>> getLatestBlock() {
        MutableLiveData<Pair<Sha256Hash, Integer>> liveBlockTip = new MutableLiveData<>();
        liveBlockTip.setValue(blockchain.getLatestBlock());
        return liveBlockTip;
    }

    public LiveData<Sha256Hash> getBlockHash(int blockHeight) {
        MutableLiveData<Sha256Hash> liveBlockHash = new MutableLiveData<>();
        liveBlockHash.setValue(blockchain.getBlockHash(blockHeight));
        return liveBlockHash;
    }

}
