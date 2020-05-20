package io.github.yzernik.squeakand.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakRepository;
import io.github.yzernik.squeakand.Todo;
import io.github.yzernik.squeakand.TodoRepository;

public class HomeViewModel extends AndroidViewModel {

    private SqueakRepository mSqueakRepository;
    private LiveData<List<SqueakEntry>> mAllSqueaks;

    public HomeViewModel(Application application) {
        super(application);
        mSqueakRepository = new SqueakRepository(application);
        mAllSqueaks = mSqueakRepository.getAllSqueaks();
    }

    LiveData<List<SqueakEntry>> getAllSqueaks() {
        return mAllSqueaks;
    }
}