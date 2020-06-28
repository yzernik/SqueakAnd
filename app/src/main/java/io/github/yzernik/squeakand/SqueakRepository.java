package io.github.yzernik.squeakand;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.squeaks.SqueakBlockVerifier;
import io.github.yzernik.squeakand.squeaks.SqueaksController;
import io.github.yzernik.squeaklib.core.Squeak;
import lnrpc.Rpc;

public class SqueakRepository {

    private static volatile SqueakRepository INSTANCE;

    private SqueakDao mSqueakDao;
    private OfferDao mOfferDao;
    private LiveData<List<SqueakEntry>> mAllSqueaks;
    private LiveData<List<SqueakEntryWithProfile>> mAllSqueaksWithProfile;
    private ElectrumBlockchainRepository electrumBlockchainRepository;
    private LndRepository lndRepository;
    private SqueaksController squeaksController;
    private SqueakBlockVerifier blockVerifier;
    private ExecutorService executorService;


    // Note that in order to unit test the TodoRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    private SqueakRepository(Application application) {
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        mSqueakDao = db.squeakDao();
        mOfferDao = db.offerDao();
        mAllSqueaks = mSqueakDao.getSqueaks();
        mAllSqueaksWithProfile = mSqueakDao.getTimelineSqueaksWithProfile();
        electrumBlockchainRepository = ElectrumBlockchainRepository.getRepository(application);
        lndRepository = LndRepository.getRepository(application);
        squeaksController = new SqueaksController(mSqueakDao, mOfferDao, electrumBlockchainRepository, lndRepository.getLndController());
        blockVerifier = new SqueakBlockVerifier(squeaksController);
        this.executorService = Executors.newCachedThreadPool();
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

    public void initialize() {
        Log.i(getClass().getName(), "Initializing squeaks repository...");

        // Start the block verifier
        blockVerifier.verifySqueakBlocks();
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

    public void insert(Squeak squeak) {
        squeaksController.save(squeak);
    }

    public void insertWithBlock(Squeak squeak, Block block) {
        squeaksController.saveWithBlock(squeak, block);
    }

    public LiveData<List<SqueakEntryWithProfile>> getThreadAncestorSqueaks(Sha256Hash hash) {
        return mSqueakDao.fetchLiveSqueakReplyAncestorsByHash(hash);
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
    }


    /*
    public LiveData<Rpc.Channel> getOfferChannel(int offerId) {
        Log.i(getClass().getName(), "Getting offer channel...");
        MutableLiveData<Rpc.Channel> liveChannelResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Offer offer = mOfferDao.fetchOfferById(offerId);
                Rpc.Channel channel = squeaksController.getChannelToOffer(offer);
                liveChannelResponse.postValue(channel);
            }
        });
        return liveChannelResponse;
    }*/

}
