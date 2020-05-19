package io.github.yzernik.squeakand;

import androidx.core.util.Pair;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.blockchain.Blockchain;
import io.github.yzernik.squeakand.blockchain.BlockchainException;

public class SqueakTestUtils {

    public static class DummyBlockchain implements Blockchain {
        private static final Sha256Hash GENESIS_BLOCK_HASH = Sha256Hash.wrap("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b");
        private static final int GENESIS_BLOCK_HEIGHT = 1;

        @Override
        public Pair<Sha256Hash, Integer> getLatestBlock() {
            return new Pair<Sha256Hash, Integer>(GENESIS_BLOCK_HASH, GENESIS_BLOCK_HEIGHT);
        }

        @Override
        public Sha256Hash getBlockHash(long blockHeight) throws BlockchainException {
            if (blockHeight == GENESIS_BLOCK_HEIGHT) {
                return GENESIS_BLOCK_HASH;
            }
            throw new BlockchainException("Unable to find block with height: " + blockHeight);
        }
    }

}
