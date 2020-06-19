package io.github.yzernik.squeakand;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.server.ServerSyncer;
import io.github.yzernik.squeakand.server.ServerUploader;
import io.github.yzernik.squeakand.server.SqueakNetworkController;
import io.github.yzernik.squeakand.server.SqueakServerAsyncClient;
import io.github.yzernik.squeakand.squeaks.SqueaksController;
import io.github.yzernik.squeaklib.core.Squeak;

/**
 * Maintains the connections to the squeak servers, and keeps synced with them.
 */
public class SqueakServerRepository {

    private static volatile SqueakServerRepository INSTANCE;

    private final Application application;
    private SqueakDao mSqueakDao;
    private SqueakProfileDao mSqueakProfileDao;
    private SqueakServerDao mSqueakServerDao;
    private SqueaksController squeaksController;
    private SqueakNetworkController squeakNetworkController;

    // TODO: make singleton.
    public SqueakServerRepository(Application application) {
        // Singleton constructor, only called by static method.
        this.application = application;
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        mSqueakDao = db.squeakDao();
        mSqueakProfileDao = db.squeakProfileDao();
        mSqueakServerDao = db.squeakServerDao();
        ElectrumBlockchainRepository electrumBlockchainRepository = ElectrumBlockchainRepository.getRepository(application);
        squeaksController = new SqueaksController(mSqueakDao, electrumBlockchainRepository);
        squeakNetworkController = new SqueakNetworkController(squeaksController, mSqueakProfileDao, mSqueakServerDao);
    }

    public static SqueakServerRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (SqueakServerRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SqueakServerRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public void initialize() {
        Log.i(getClass().getName(), "Initializing squeak server connections...");

        // Start the sync thread
        ServerSyncer syncer = new ServerSyncer(squeakNetworkController);
        syncer.startSyncTask();

        // Start the upload thread
        ServerUploader uploader = new ServerUploader(squeakNetworkController);
        uploader.startUploadTask();
    }

    public void publishSqueak(Squeak squeak) {
        squeakNetworkController.enqueueToPublish(squeak);
    }

    public void insert(SqueakServer server) {
        SqueakRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSqueakServerDao.insert(server);
        });
    }

    public LiveData<SqueakServer> getSqueakServer(int id) {
        return mSqueakServerDao.fetchServerById(id);
    }

    public void update(SqueakServer server) {
        SqueakRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSqueakServerDao.update(server);
        });
    }

    public void delete(SqueakServer server) {
        SqueakRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSqueakServerDao.delete(server);
        });
    }

    public LiveData<List<SqueakServer>> getLiveServers() {
        return mSqueakServerDao.getLiveServers();
    }

    public SqueakServerAsyncClient getSqueakServerAsyncClient() {
        return new SqueakServerAsyncClient(squeakNetworkController);
    }

}
