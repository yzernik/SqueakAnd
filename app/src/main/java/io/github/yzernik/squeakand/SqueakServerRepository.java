package io.github.yzernik.squeakand;

import android.app.Application;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;

import io.github.yzernik.squeakand.server.ServerSyncer;
import io.github.yzernik.squeakand.server.SqueakNetworkController;
import io.github.yzernik.squeakand.server.SqueakServer;
import io.github.yzernik.squeakand.server.SqueakServerAddress;
import io.github.yzernik.squeaklib.core.Squeak;

/**
 * Maintains the connections to the squeak servers, and keeps synced with them.
 */
public class SqueakServerRepository {

    private static volatile SqueakServerRepository INSTANCE;

    private final Application application;
    private final ConcurrentHashMap<SqueakServerAddress, SqueakServer> servers = new ConcurrentHashMap<>();
    // private SqueakServerDao mSqueakServerDao;
    private SqueakDao mSqueakDao;
    private SqueakProfileDao mSqueakProfileDao;
    private SqueakNetworkController squeakNetworkController;

    private SqueakServerRepository(Application application) {
        // Singleton constructor, only called by static method.
        this.application = application;
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        // mSqueakServerDao = db.squeakServerDao();
        mSqueakDao = db.squeakDao();
        mSqueakProfileDao = db.squeakProfileDao();
        squeakNetworkController = new SqueakNetworkController(mSqueakDao, mSqueakProfileDao);
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
    }

    public void publishSqueak(Squeak squeak) {
        squeakNetworkController.publish(squeak);
    }

}
