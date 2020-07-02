package io.github.yzernik.squeakand;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.blockchain.ElectrumConnection;
import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.server.ServerSyncer;
import io.github.yzernik.squeakand.server.ServerUploader;
import io.github.yzernik.squeakand.server.SqueakNetworkAsyncClient;
import io.github.yzernik.squeakand.server.SqueakNetworkController;
import io.github.yzernik.squeakand.squeaks.SqueakBlockVerifier;
import io.github.yzernik.squeakand.squeaks.SqueaksController;
import io.github.yzernik.squeaklib.core.Squeak;
import lnrpc.Rpc;

public class SqueakControllerRepository {

    private static volatile SqueakControllerRepository INSTANCE;

    // DAOs
    private SqueakDao mSqueakDao;
    private OfferDao mOfferDao;
    private SqueakProfileDao mSqueakProfileDao;
    private SqueakServerDao mSqueakServerDao;

    private ElectrumBlockchainRepository electrumBlockchainRepository;
    private LndRepository lndRepository;
    private SqueaksController squeaksController;
    private SqueakBlockVerifier blockVerifier;
    private SqueakNetworkController squeakNetworkController;
    private SqueakNetworkAsyncClient asyncClient;
    private ElectrumConnection electrumConnection;
    private ExecutorService executorService;

    private SqueakControllerRepository(Application application) {
        // Singleton constructor, only called by static method.
        SqueakRoomDatabase db = SqueakRoomDatabase.getDatabase(application);
        mSqueakDao = db.squeakDao();
        mOfferDao = db.offerDao();
        mSqueakProfileDao = db.squeakProfileDao();
        mSqueakServerDao = db.squeakServerDao();

        electrumBlockchainRepository = ElectrumBlockchainRepository.getRepository(application);
        lndRepository = LndRepository.getRepository(application);
        squeaksController = new SqueaksController(mSqueakDao, mOfferDao, electrumBlockchainRepository, lndRepository.getLndSyncClient());
        squeakNetworkController = new SqueakNetworkController(squeaksController, mSqueakProfileDao, mSqueakServerDao);
        electrumConnection = electrumBlockchainRepository.getElectrumConnection();
        executorService = Executors.newCachedThreadPool();
        asyncClient = new SqueakNetworkAsyncClient(squeakNetworkController, electrumConnection, executorService);

        blockVerifier = new SqueakBlockVerifier(squeaksController, executorService);
    }

    public static SqueakControllerRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (SqueakControllerRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SqueakControllerRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public void initialize() {
        Log.i(getClass().getName(), "Initializing squeaks repository...");

        // Start the block verifier
        blockVerifier.verifySqueakBlocks();

        // Start the sync thread
        ElectrumConnection electrumConnection = electrumBlockchainRepository.getElectrumConnection();
        ServerSyncer syncer = new ServerSyncer(squeakNetworkController, electrumConnection, executorService);
        syncer.startSyncTask();

        // Start the upload thread
        ServerUploader uploader = new ServerUploader(squeakNetworkController, executorService);
        uploader.startUploadTask();
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

    /**
     * Send a payment for the given offer id, and if the payment is
     * successful, update the offer record and squeak record in the
     * database.
     *
     * Return the SendResponse from the payment.
     * @param offerId Id of the offer to buy.
     * @return SendResponse
     */
    public LiveData<DataResult<Rpc.SendResponse>> buyOffer(int offerId) {
        Log.i(getClass().getName(), "Buying offer...");
        MutableLiveData<DataResult<Rpc.SendResponse>> liveSendResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Offer offer = mOfferDao.fetchOfferById(offerId);
                DataResult<Rpc.SendResponse> result = squeaksController.payOffer(offer);
                liveSendResponse.postValue(result);

                // TODO: this can be removed when SubscribeHtlcEvents is working.
                lndRepository.updateChannels();
            }
        });
        return liveSendResponse;
    }

    public void publishSqueak(Squeak squeak) {
        squeakNetworkController.enqueueToPublish(squeak);
    }

    public SqueakNetworkAsyncClient getSqueakServerAsyncClient() {
        return asyncClient;
    }

    public LiveData<DataResult<Integer>> syncTimeline() {
        Log.i(getClass().getName(), "Syncing timeline...");
        MutableLiveData<DataResult<Integer>> liveSendResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int count = squeakNetworkController.sync(electrumConnection);
                    liveSendResponse.postValue(DataResult.ofSuccess(count));
                } catch (Exception e) {
                    e.printStackTrace();
                    liveSendResponse.postValue(DataResult.ofFailure(e));
                }
            }
        });
        return liveSendResponse;
    }

}
