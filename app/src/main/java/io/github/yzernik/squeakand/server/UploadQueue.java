package io.github.yzernik.squeakand.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.github.yzernik.squeaklib.core.Squeak;

public class UploadQueue {

    private final BlockingQueue<Squeak> queue;

    public UploadQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public void addSqueakToUpload(Squeak squeak) {
        queue.add(squeak);
    }

    public Squeak getNextSqueakToUpload() throws InterruptedException {
        return queue.take();
    }

}
