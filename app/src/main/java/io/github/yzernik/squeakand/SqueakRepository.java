package io.github.yzernik.squeakand;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

public class SqueakRepository {

    private SqueakDao mSqueakDao;
    private LiveData<List<SqueakEntry>> mAllSqueaks;
    private LiveData<List<SqueakEntryWithProfile>> mAllSqueaksWithProfile;

    // Note that in order to unit test the TodoRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public SqueakRepository(Application application) {
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        mSqueakDao = db.squeakDao();
        mAllSqueaks = mSqueakDao.getSqueaks();
        mAllSqueaksWithProfile = mSqueakDao.getSqueaksWithProfile();
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
    public void insert(SqueakEntry squeakEntry) {
        SqueakRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSqueakDao.insert(squeakEntry);
        });
    }

}
