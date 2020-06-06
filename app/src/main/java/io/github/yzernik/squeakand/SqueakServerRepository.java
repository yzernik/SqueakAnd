package io.github.yzernik.squeakand;

import android.app.Application;
import android.util.Log;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.github.yzernik.squeakand.server.ServerSyncer;
import io.github.yzernik.squeakand.server.SqueakServer;
import io.github.yzernik.squeakand.server.SqueakServerAddress;
import io.github.yzernik.squeaklib.core.Squeak;

/**
 * Maintains the connections to the squeak servers, and keeps synced with them.
 */
public class SqueakServerRepository {

    private static volatile SqueakServerRepository INSTANCE;

    private final ConcurrentHashMap<SqueakServerAddress, SqueakServer> servers = new ConcurrentHashMap<>();
    // private SqueakServerDao mSqueakServerDao;
    private SqueakDao mSqueakDao;

    private SqueakServerRepository(Application application) {
        // Singleton constructor, only called by static method.
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        // mSqueakServerDao = db.squeakServerDao();
        mSqueakDao = db.squeakDao();
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

        // Start the peer discovery
        // peerDownloader.keepPeersUpdated();

        // Keep the map of servers synced with the database.
        // TODO: use the db here

        // Start the sync thread
        ServerSyncer syncer = new ServerSyncer(mSqueakDao);
        syncer.startSyncTask();

        SqueakServerAddress localServer = new SqueakServerAddress("10.0.2.2", 8774);
        List<SqueakServerAddress> servers = Arrays.asList(localServer);
        Log.i(getClass().getName(), "Setting syncer servers with only local server");
        syncer.setServers(servers);
    }

    public void publishSqueak(Squeak squeak) {
        // TODO: upload the squeak to all of the servers.
    }
}
