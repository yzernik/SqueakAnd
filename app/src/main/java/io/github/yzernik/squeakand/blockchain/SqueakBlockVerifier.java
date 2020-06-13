package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SqueakBlockVerifier {

    // TODO: verify each squeak and add the block header to the database entry.

    /*    private final ElectrumDownloaderController downloaderController;
    private final ExecutorService executorService;
    private Future<String> future = null;

    SqueakBlockVerifier(ElectrumDownloaderController downloaderController) {
        this.downloaderController = downloaderController;
        this.executorService = Executors.newCachedThreadPool();
    }

    synchronized void reset() {
        if (future != null) {
            future.cancel(true);
        }

        ElectrumServerAddress serverAddress = downloaderController.getCurrentDownloadServer();
        // Start a new download task if the current server address is not null.
        if (serverAddress != null) {
            BlockDownloader.BlockDownloadTask newDownloadTask = new BlockDownloader.BlockDownloadTask(serverAddress);
            Log.i(getClass().getName(), "Submitting new download task.");
            future = executorService.submit(newDownloadTask);
        }
    }*/

}
