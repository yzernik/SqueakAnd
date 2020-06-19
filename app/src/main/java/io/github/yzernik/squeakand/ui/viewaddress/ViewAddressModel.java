package io.github.yzernik.squeakand.ui.viewaddress;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakRepository;

public class ViewAddressModel extends AndroidViewModel {

    private SqueakRepository mSqueakRepository;

    public ViewAddressModel(@NonNull Application application) {
        super(application);
        mSqueakRepository = SqueakRepository.getRepository(application);
    }

    LiveData<List<SqueakEntryWithProfile>> getmAllAddressSqueaksWithProfile(String address) {
        return mSqueakRepository.getSqueaksByAuthor(address);
    }

}
