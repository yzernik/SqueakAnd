package io.github.yzernik.squeakand;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

public class SqueakRepository {

    private static volatile SqueakRepository INSTANCE;

    private SqueakDao mSqueakDao;
    private LiveData<List<SqueakEntry>> mAllSqueaks;
    private LiveData<List<SqueakEntryWithProfile>> mAllSqueaksWithProfile;

    // Note that in order to unit test the TodoRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    private SqueakRepository(Application application) {
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        mSqueakDao = db.squeakDao();
        mAllSqueaks = mSqueakDao.getSqueaks();
        mAllSqueaksWithProfile = mSqueakDao.getTimelineSqueaksWithProfile();
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

    public LiveData<List<SqueakEntryWithProfile>> getSqueaksByAuthor(String address) {
        return mSqueakDao.fetchLiveSqueaksByAddress(address);
    }

    public LiveData<List<SqueakEntryWithProfile>> getThreadAncestorSqueaks(Sha256Hash hash) {
        return mSqueakDao.fetchLiveSqueakReplyAncestorsByHash(hash);
    }

    /*
    public void insert(Squeak squeak) {
        squeaksController.save(squeak);
    }

    public void insertWithBlock(Squeak squeak, Block block) {
        squeaksController.saveWithBlock(squeak, block);
    }

    public SqueaksController getController() {
        return squeaksController;
    }

    public LiveData<Rpc.SendResponse> buyOffer(int offerId) {
        Log.i(getClass().getName(), "Buying offer...");
        MutableLiveData<Rpc.SendResponse> liveSendResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Offer offer = mOfferDao.fetchOfferById(offerId);
                Rpc.SendResponse response = squeaksController.payOffer(offer);
                liveSendResponse.postValue(response);
            }
        });
        return liveSendResponse;
    }

    public LiveData<Rpc.ConnectPeerResponse> connectPeer(int offerId) {
        Log.i(getClass().getName(), "Connecting peer...");
        MutableLiveData<Rpc.ConnectPeerResponse> liveConnectPeerResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Offer offer = mOfferDao.fetchOfferById(offerId);
                Rpc.ConnectPeerResponse response = squeaksController.connectPeerToOffer(offer);
                liveConnectPeerResponse.postValue(response);
            }
        });
        return liveConnectPeerResponse;
    }

    public LiveData<Rpc.ChannelPoint> openOfferChannel(int offerId, long amount) {
        Log.i(getClass().getName(), "Opening channel...");
        MutableLiveData<Rpc.ChannelPoint> liveOpenChannelResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Offer offer = mOfferDao.fetchOfferById(offerId);
                // Open the channel to the node
                Rpc.ChannelPoint response = squeaksController.openChannelToOffer(offer, amount);

                liveOpenChannelResponse.postValue(response);
            }
        });
        return liveOpenChannelResponse;
    }*/


}
