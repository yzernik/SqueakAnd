package io.github.yzernik.squeakand.squeaks;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SqueakBlockVerifier {

    // TODO: verify each squeak and add the block header to the database entry.
    private final ExecutorService executorService;
    private Future<String> future = null;

    private SqueaksController squeaksController;

    public SqueakBlockVerifier(SqueaksController squeaksController) {
        this.squeaksController = squeaksController;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public synchronized void verifySqueakBlocks() {
        if (future != null) {
            future.cancel(true);
        }

        VerifyTask verifyTask = new VerifyTask();
        Log.i(getClass().getName(), "Submitting new verify task.");
        future = executorService.submit(verifyTask);
    }


    class VerifyTask implements Callable<String> {

        VerifyTask() {
        }

        @Override
        public String call() throws InterruptedException {
            Log.i(getClass().getName(), "Calling call.");

            squeaksController.verifyAllEnqueued();
            return null;
        }

    }

}
