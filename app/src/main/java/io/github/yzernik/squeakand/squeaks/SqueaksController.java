package io.github.yzernik.squeakand.squeaks;

import android.util.Log;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferDao;
import io.github.yzernik.squeakand.SqueakDao;
import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakRoomDatabase;
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeaklib.core.Squeak;
import io.github.yzernik.squeaklib.core.VerificationException;

public class SqueaksController {

    private static final long GET_BLOCK_HEADER_TIMEOUT_S = 10;

    private SqueakDao mSqueakDao;
    private OfferDao offerDao;
    private SqueakBlockVerificationQueue verificationQueue;
    private ElectrumBlockchainRepository electrumBlockchainRepository;


    public SqueaksController(SqueakDao mSqueakDao, OfferDao offerDao, ElectrumBlockchainRepository electrumBlockchainRepository) {
        this.mSqueakDao = mSqueakDao;
        this.offerDao = offerDao;
        this.verificationQueue = new SqueakBlockVerificationQueue();
        this.electrumBlockchainRepository = electrumBlockchainRepository;
    }

    /**
     * Continually verify squeaks that have been added to the verification queue.
     *
     * This method does not stop until interrupted.
     * @throws InterruptedException
     */
    public void verifyAllEnqueued() throws InterruptedException {
        while (true) {
            Squeak squeakToVerify = verificationQueue.getNextSqueakToVerify();
            Log.i(getClass().getName(), "Verifying squeak from queue: " + squeakToVerify);
            verifyBlock(squeakToVerify);
        }
    }

    /**
     * Try to verify all unverified squeaks in the database.
     * @throws InterruptedException
     */
    public void verifyOldSqueaks() throws InterruptedException {
        // Get the list of unverified squeaks in the database.
        List<SqueakEntry> squeakEntries = mSqueakDao.fetchUnverifiedSqueaks();
        for (SqueakEntry squeakEntry: squeakEntries) {
            Squeak squeakToVerify = squeakEntry.getSqueak();
            verifyBlock(squeakToVerify);
        }
    }

    private Block getBlockHeader(int blockHeight) throws InterruptedException, ExecutionException, TimeoutException {
        Future<Block> futureBlock = electrumBlockchainRepository.getBlockHash(blockHeight);
        return futureBlock.get(GET_BLOCK_HEADER_TIMEOUT_S, TimeUnit.SECONDS);
    }

    // Make an electrum request for the given block height and check if it matches.
    private void verifyBlock(Squeak squeak) throws InterruptedException {
        Log.i(getClass().getName(), "Trying to verify squeak with hash: " + squeak.getHash());
        int blockHeight = (int) squeak.getBlockHeight();
        Block block = null;
        try {
            block = getBlockHeader(blockHeight);
            Log.i(getClass().getName(), "Local block hash : " + squeak.getHashBlock());
            Log.i(getClass().getName(), "Electrum block hash : " + block.getHash());
            if (block.getHash().equals(squeak.getHashBlock())) {
                markAsVerified(squeak, block);
            }
        } catch (ExecutionException | TimeoutException e) {
            e.printStackTrace();
            Log.e(getClass().getName(), "Failed to get block header for squeak: " + squeak);
        }

    }

    /**
     * Update the database entry for the given squeak with the valid block header.
     */
    private void markAsVerified(Squeak squeak, Block block) {
        Log.i(getClass().getName(), "Setting squeak as verified: " + squeak.getHash());
        // Update the squeak entry in the database with the matching block.
        SqueakEntry squeakEntry = new SqueakEntry(squeak, block);
        mSqueakDao.update(squeakEntry);
    }

    public void save(Squeak squeak) {
        // Validate the squeak
        try {
            validateSqueak(squeak);
        } catch (VerificationException e) {
            e.printStackTrace();
            return;
        }

        // TODO: Remove this after testing
        squeak.clearDataKey();

        // Insert the squeak in the database.
        SqueakEntry squeakEntry = new SqueakEntry(squeak);
        SqueakRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSqueakDao.insert(squeakEntry);
        });

        // Add the squeak to the block verification queue
        verificationQueue.addSqueakToVerify(squeak);
    }

    public void saveWithBlock(Squeak squeak, Block block) {
        // Validate the squeak
        try {
            validateSqueak(squeak);
        } catch (VerificationException e) {
            e.printStackTrace();
            return;
        }

        // Insert the squeak in the database.
        SqueakEntry squeakEntry = new SqueakEntry(squeak);
        SqueakRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Insert the squeak
            mSqueakDao.insert(squeakEntry);
            // Mark the squeak as verified
            markAsVerified(squeak, block);
        });
    }

    public List<SqueakEntry> fetchSqueaksByAddress(String address) {
        return mSqueakDao.fetchSqueaksByAddress(address);
    }

    public SqueakEntryWithProfile fetchSqueakWithProfileByHash(Sha256Hash squeakHash) {
        return mSqueakDao.fetchSqueakWithProfileByHash(squeakHash);
    }

    /**
     * Rase an exception if the squeak is invalid.
     * @param squeak
     * @throws VerificationException
     */
    private void validateSqueak(Squeak squeak) throws VerificationException {
        if(squeak.getDataKey() == null) {
            squeak.verify(true);
        } else {
            squeak.verify(false);
        }
    }

    public void saveOffer(Offer offer) {
        offerDao.insert(offer);
    }

    public void getOfferForSqueakAndServer(Sha256Hash squeakHash, int serverId) {
        List<Offer> offers = offerDao.fetchOffersBySqueakHashAndServerId(squeakHash, serverId);
    }

}
