package io.github.yzernik.squeakand;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.server.SqueakServerAddress;

/**
 * Maintains the connections to the squeak servers, and keeps synced with them.
 */
public class SqueakServerRepository {

    private static volatile SqueakServerRepository INSTANCE;

    private SqueakServerDao mSqueakServerDao;

    private SqueakServerRepository(Application application) {
        // Singleton constructor, only called by static method.
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        mSqueakServerDao = db.squeakServerDao();
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

    public void insert(SqueakServer server) {
        SqueakRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSqueakServerDao.insert(server);
        });
    }

    public LiveData<SqueakServer> getSqueakServer(int id) {
        return mSqueakServerDao.fetchServerById(id);
    }

    public LiveData<SqueakServer> getSqueakServerByAddress(SqueakServerAddress serverAddress) {
        return mSqueakServerDao.fetchServerByAddress(serverAddress);
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

}
