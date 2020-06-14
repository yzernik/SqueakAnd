package io.github.yzernik.squeakand.squeaks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.github.yzernik.squeaklib.core.Squeak;

public class SqueakBlockVerificationQueue {

    private final BlockingQueue<Squeak> queue;

    public SqueakBlockVerificationQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public void addSqueakToVerify(Squeak squeak) {
        queue.add(squeak);
    }

    public Squeak getNextSqueakToVerify() throws InterruptedException {
        return queue.take();
    }

}
