package io.github.yzernik.squeakand;

import android.app.Application;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.server.ServerSyncer;
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

    private SqueakServerRepository(Application application) {
        // Singleton constructor, only called by static method.
        this.application = application;
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        // mSqueakServerDao = db.squeakServerDao();
        mSqueakDao = db.squeakDao();
        mSqueakProfileDao = db.squeakProfileDao();
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
        ServerSyncer syncer = new ServerSyncer(mSqueakDao, mSqueakProfileDao);
        syncer.startSyncTask();


        /*        // Set the servers
        List<SqueakServerAddress> servers = getServers();
        Log.i(getClass().getName(), "Setting syncer servers with only local server");
        syncer.setServers(servers);

        // Set the upload profiles
        List<String> signingProfiles = getUploadAddresses();
        syncer.setUploadAddresses(signingProfiles);*/

    }

    public void publishSqueak(Squeak squeak) {
        // TODO: upload the squeak to all of the servers.
    }

/*    private List<SqueakServerAddress> getServers() {
        SqueakServerAddress localServer = new SqueakServerAddress("10.0.2.2", 8774);
        return Arrays.asList(localServer);
    }*/

/*    private List<String> getUploadAddresses() {
        List<SqueakProfile> signingProfiles = mSqueakProfileDao.getSigningProfiles();
        Log.i(getClass().getName(), "Got number of signing profiles: " + signingProfiles.size());
        Log.i(getClass().getName(), "Got signing profiles: " + signingProfiles);
        for (SqueakProfile profile: signingProfiles) {
            Log.i(getClass().getName(), "Got signing profile: " + profile);
        }


        return signingProfiles.stream()
                .map(profile -> profile.getAddress())
                .collect(Collectors.toList());
    }*/
}
