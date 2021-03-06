package io.github.yzernik.squeakand.squeaks;

import android.util.Log;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferDao;
import io.github.yzernik.squeakand.SqueakDao;
import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakRoomDatabase;
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeakand.crypto.CryptoUtil;
import io.github.yzernik.squeakand.lnd.LndSyncClient;
import io.github.yzernik.squeakand.server.SqueakServerAddress;
import io.github.yzernik.squeaklib.core.Encryption;
import io.github.yzernik.squeaklib.core.EncryptionException;
import io.github.yzernik.squeaklib.core.Squeak;
import io.github.yzernik.squeaklib.core.VerificationException;
import lnrpc.Rpc;

public class SqueaksController {

    private static final long GET_BLOCK_HEADER_TIMEOUT_S = 10;

    private SqueakDao mSqueakDao;
    private OfferDao offerDao;
    private SqueakBlockVerificationQueue verificationQueue;
    private ElectrumBlockchainRepository electrumBlockchainRepository;
    private LndSyncClient lndSyncClient;


    public SqueaksController(SqueakDao mSqueakDao, OfferDao offerDao, ElectrumBlockchainRepository electrumBlockchainRepository, LndSyncClient lndSyncClient) {
        this.mSqueakDao = mSqueakDao;
        this.offerDao = offerDao;
        this.verificationQueue = new SqueakBlockVerificationQueue();
        this.electrumBlockchainRepository = electrumBlockchainRepository;
        this.lndSyncClient = lndSyncClient;
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
            if (block == null) {
                Log.i(getClass().getName(), "Failed to get block header for block height : " + blockHeight);
                return;
            }

            Log.i(getClass().getName(), "Local block hash : " + squeak.getHashBlock());
            Log.i(getClass().getName(), "Electrum block hash : " + block.getHash());
            if (block.getHash().equals(squeak.getHashBlock())) {
                markAsVerified(squeak, block);
            }
        } catch (ExecutionException | TimeoutException e) {
            e.printStackTrace();
            Log.e(getClass().getName(), "Failed to get block header for squeak: " + squeak);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(getClass().getName(), "Failed in verifyBlock: " + e);
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

        // Insert the squeak in the database.
        SqueakEntry squeakEntry = new SqueakEntry(squeak);
        SqueakRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSqueakDao.insert(squeakEntry);
            Log.i(getClass().getName(), "Inserted squeak: " + squeak);
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

    public void setDecryptionKey(SqueakEntry squeakEntry, byte[] preimage) {
        Log.i(getClass().getName(), "Setting data key for squeak: " + squeakEntry.getSqueak().getHash());
        Squeak squeak = squeakEntry.getSqueak();

        // TODO: set the actual decryption key, not the preimage.
        squeak.setDecryptionKey(preimage);
        try {
            squeak.verify();
            SqueakEntry newSqueakEntry = new SqueakEntry(squeak, squeakEntry.block);
            mSqueakDao.update(newSqueakEntry);
        } catch (VerificationException e) {
            e.printStackTrace();
        }
    }

    public void setOfferHasValidPreimage(Offer offer, boolean hasValidPreimage) {
        offer.setHasValidPreimage(hasValidPreimage);
        offerDao.update(offer);
    }

    public List<SqueakEntry> fetchSqueaksByAddress(String address, int minBlock, int maxBlock) {
        return mSqueakDao.fetchSqueaksByAddress(address, minBlock, maxBlock);
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
        if(squeak.hasDecryptionKey()) {
            squeak.verify(false);
        } else {
            squeak.verify(true);
        }
    }

    public void saveOffer(Offer offer) {
        offerDao.insert(offer);
    }

    public Offer getOfferForSqueakAndServer(Sha256Hash squeakHash, SqueakServerAddress squeakServerAddress) {
        return offerDao.fetchOfferBySqueakHashAndServerAddress(squeakHash, squeakServerAddress);
    }

    public void deleteOffer(Offer offer) {
        offerDao.delete(offer);
    }

    public DataResult<Rpc.SendResponse> payOffer(Offer offer) {
        Rpc.SendResponse sendResponse = null;
        try {
            sendResponse = lndSyncClient.sendPayment(offer.paymentRequest);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return DataResult.ofFailure(e);
        }
        Log.e(getClass().getName(), "sendResponse.getPaymentPreimage: " + sendResponse.getPaymentPreimage());
        if (sendResponse.getPaymentPreimage().isEmpty()) {
            // Handle failed payment
            Log.e(getClass().getName(), "Failed payment: " + sendResponse.getPaymentError());
        } else{
            // Handle successful payment
            byte[] preimage = sendResponse.getPaymentPreimage().toByteArray();

            // Set the offer as complete
            offer.setPreimage(preimage);

            // Set the squeak data key if valid
            byte[] keyCipherBytes = offer.keyCipher;
            byte[] iv = offer.iv;
            Encryption.EncryptedDecryptionKey encryptedDecryptionKey = Encryption.EncryptedDecryptionKey.fromBytes(keyCipherBytes);

            // Decrypt the decryption key
            try {
                Encryption.DecryptionKey decryptionKey = encryptedDecryptionKey.getDecryptionKey(preimage, iv);

                SqueakEntry squeakEntry = mSqueakDao.fetchSqueakByHash(offer.squeakHash);

                Squeak squeak = squeakEntry.getSqueak();
                if (isValidDecryptionKey(squeak, decryptionKey.getBytes())) {
                    setOfferHasValidPreimage(offer, true);
                    setDecryptionKey(squeakEntry, decryptionKey.getBytes());
                }
            } catch (EncryptionException e) {
                e.printStackTrace();
                return DataResult.ofFailure(e);
            }
        }
        return DataResult.ofSuccess(sendResponse);
    }

    private boolean isValidDecryptionKey(Squeak squeak, byte[] decryptionKey) {
        squeak.setDecryptionKey(decryptionKey);
        try {
            squeak.verify();
            return true;
        } catch (VerificationException e) {
            return false;
        }
    }

}
