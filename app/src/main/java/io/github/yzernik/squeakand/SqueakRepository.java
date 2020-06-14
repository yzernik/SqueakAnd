package io.github.yzernik.squeakand;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.squeaks.SqueakBlockVerificationQueue;
import io.github.yzernik.squeakand.squeaks.SqueakBlockVerifier;
import io.github.yzernik.squeakand.squeaks.SqueaksController;
import io.github.yzernik.squeaklib.core.Squeak;

public class SqueakRepository {

    private static volatile SqueakRepository INSTANCE;

    private SqueakDao mSqueakDao;
    private LiveData<List<SqueakEntry>> mAllSqueaks;
    private LiveData<List<SqueakEntryWithProfile>> mAllSqueaksWithProfile;
    private SqueakBlockVerificationQueue verificationQueue;
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
        mAllSqueaksWithProfile = mSqueakDao.getSqueaksWithProfile();
        verificationQueue = new SqueakBlockVerificationQueue();
        electrumBlockchainRepository = ElectrumBlockchainRepository.getRepository(application);
        squeaksController = new SqueaksController(mSqueakDao, verificationQueue, electrumBlockchainRepository);
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

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Squeak squeak) {
        // TODO: handle everything here inside the controller.

        // Validate the squeak
        squeak.verify();

        // Insert the squeak in the database.
        SqueakEntry squeakEntry = new SqueakEntry(squeak);
        SqueakRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSqueakDao.insert(squeakEntry);
        });

        // Add the squeak to the block verification queue
        verificationQueue.addSqueakToVerify(squeak);
    }

}
