package io.github.yzernik.squeakand;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SqueakProfileRepository {


    private SqueakProfileDao mSqueakProfileDao;
    private LiveData<List<SqueakProfile>> mAllSqueakProfiles;

    // Note that in order to unit test the TodoRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public SqueakProfileRepository(Application application) {
        TodoRoomDatabase db = TodoRoomDatabase.getDatabase(application);
        mSqueakProfileDao = db.squeakProfileDao();
        mAllSqueakProfiles = mSqueakProfileDao.getProfiles();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<SqueakProfile>> getAllSqueakProfiles() {
        return mAllSqueakProfiles;
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<SqueakProfile> getSqueakProfile(int id) {
        return mSqueakProfileDao.fetchProfileById(id);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(SqueakProfile squeakProfile) {
        TodoRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSqueakProfileDao.insert(squeakProfile);
        });
    }

}
