package io.github.yzernik.squeakand.squeaks;

import android.util.Log;

import org.bitcoinj.core.Block;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.yzernik.squeakand.SqueakDao;
import io.github.yzernik.squeakand.blockchain.ElectrumBlockchainRepository;
import io.github.yzernik.squeaklib.core.Squeak;

public class SqueaksController {

    private static final long GET_BLOCK_HEADER_TIMEOUT_S = 10;

    private SqueakDao mSqueakDao;
    private SqueakBlockVerificationQueue verificationQueue;
    private ElectrumBlockchainRepository electrumBlockchainRepository;


    public SqueaksController(SqueakDao mSqueakDao, SqueakBlockVerificationQueue verificationQueue, ElectrumBlockchainRepository electrumBlockchainRepository) {
        this.mSqueakDao = mSqueakDao;
        this.verificationQueue = verificationQueue;
        this.electrumBlockchainRepository = electrumBlockchainRepository;
    }

    public void verifyAllEnqueued() throws InterruptedException {
        while (true) {
            Squeak squeakToVerify = verificationQueue.getNextSqueakToVerify();
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
                markAsVerified(squeak);
            }
        } catch (ExecutionException | TimeoutException e) {
            e.printStackTrace();
            Log.e(getClass().getName(), "Failed to get block header for squeak: " + squeak);
        }

    }

    // Update the database entry for the given squeak with the valid block header.
    private void markAsVerified(Squeak squeak) {
        Log.i(getClass().getName(), "Setting squeak as verified: " + squeak);
        // TODO
    }

}
