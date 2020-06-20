package io.github.yzernik.squeakand;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.squeaks.SqueakBlockVerifier;
import io.github.yzernik.squeakand.squeaks.SqueaksController;
import io.github.yzernik.squeaklib.core.Squeak;

public class SqueakRepository {

    private static volatile SqueakRepository INSTANCE;

    private SqueakDao mSqueakDao;
    private LiveData<List<SqueakEntry>> mAllSqueaks;
    private LiveData<List<SqueakEntryWithProfile>> mAllSqueaksWithProfile;
    private ElectrumBlockchainRepository electrumBlockchainRepository;
    private SqueaksController squeaksController;
    private SqueakBlockVerifier blockVerifier;

    // Note that in order to unit test the TodoRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    private SqueakRepository(Application application) {
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        mSqueakDao = db.squeakDao();
        mAllSqueaks = mSqueakDao.getSqueaks();
        mAllSqueaksWithProfile = mSqueakDao.getTimelineSqueaksWithProfile();
        electrumBlockchainRepository = ElectrumBlockchainRepository.getRepository(application);
        squeaksController = new SqueaksController(mSqueakDao, electrumBlockchainRepository);
        blockVerifier = new SqueakBlockVerifier(squeaksController);
    }

    public static SqueakRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (SqueakRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SqueakRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public void initialize() {
        Log.i(getClass().getName(), "Initializing squeaks repository...");

        // Start the block verifier
        blockVerifier.verifySqueakBlocks();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<SqueakEntry>> getAllSqueaks() {
        return mAllSqueaks;
    }

    public LiveData<List<SqueakEntryWithProfile>> getAllSqueaksWithProfile() {
        return mAllSqueaksWithProfile;
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<SqueakEntryWithProfile> getSqueak(Sha256Hash hash) {
        return  mSqueakDao.fetchLiveSqueakByHash(hash);
    }

    public LiveData<List<SqueakEntryWithProfile>> getSqueaksByAuthor(String address) {
        return mSqueakDao.fetchLiveSqueaksByAddress(address);
    }

    public void insert(Squeak squeak) {
        squeaksController.save(squeak);
    }

    public void insertWithBlock(Squeak squeak, Block block) {
        squeaksController.saveWithBlock(squeak, block);
    }

    public LiveData<List<SqueakEntryWithProfile>> getThreadAncestorSqueaks(Sha256Hash hash) {
        return mSqueakDao.fetchLiveSqueakReplyAncestorsByHash(hash);
    }

    public SqueaksController getController() {
        return squeaksController;
    }

}
