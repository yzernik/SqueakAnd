package io.github.yzernik.squeakand.blockchain;


public class BlockchainException extends RuntimeException {
    public BlockchainException(String msg) {
        super(msg);
    }

    public BlockchainException(Exception e) {
        super(e);
    }

    public BlockchainException(String msg, Throwable t) {
        super(msg, t);
    }

}