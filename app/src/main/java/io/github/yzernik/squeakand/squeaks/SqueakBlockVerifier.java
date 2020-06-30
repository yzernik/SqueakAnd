package io.github.yzernik.squeakand.squeaks;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;


public class SqueakBlockVerifier {

    private static final int DEFAULT_VERIFY_SLEEP_INTERVAL_MS = 60000;

    private final ExecutorService executorService;

    private SqueaksController squeaksController;

    public SqueakBlockVerifier(SqueaksController squeaksController, ExecutorService executorService) {
        this.squeaksController = squeaksController;
        this.executorService = executorService;
    }

    public synchronized void verifySqueakBlocks() {
        // Start the verifyNewSqueaks task
        VerifyNewSqueaksTask verifyNewSqueaksTask = new VerifyNewSqueaksTask();
        Log.i(getClass().getName(), "Submitting verifyNewSqueaksTask.");
        executorService.submit(verifyNewSqueaksTask);

        // Start the verifyOldSqueaks task
        VerifyOldSqueaksTask verifyOldSqueaksTask = new VerifyOldSqueaksTask();
        Log.i(getClass().getName(), "Submitting verifyOldSqueaksTask.");
        executorService.submit(verifyOldSqueaksTask);
    }


    class VerifyNewSqueaksTask implements Callable<String> {

        VerifyNewSqueaksTask() {
        }

        @Override
        public String call() throws InterruptedException {
            Log.i(getClass().getName(), "Calling call in VerifyNewSqueaksTask.");
            squeaksController.verifyAllEnqueued();
            return null;
        }

    }


    class VerifyOldSqueaksTask implements Callable<String> {

        VerifyOldSqueaksTask() {
        }

        @Override
        public String call() throws InterruptedException {
            Log.i(getClass().getName(), "Calling call.");
            while (true) {
                try {
                    Log.i(getClass().getName(), "Calling VerifyOldSqueaksTask.");
                    squeaksController.verifyOldSqueaks();
                    // Sleep until the next verify.
                    Thread.sleep(DEFAULT_VERIFY_SLEEP_INTERVAL_MS);
                } catch (Exception e) {
                    Log.e(getClass().getName(), "Failed in calling VerifyOldSqueaksTask: " + e);
                }
            }
        }

    }

}
