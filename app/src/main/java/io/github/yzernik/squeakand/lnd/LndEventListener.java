package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import lnrpc.Rpc;

public class LndEventListener {

    private static final int NUM_THREADS = 3;

    private LndClient lndClient;
    private LndSyncClient lndSyncClient;
    private LndSubscriptionEventHandler lndSubscriptionEventHandler;
    private ExecutorService executorService;

    public LndEventListener(LndClient lndClient, LndSyncClient lndSyncClient, LndSubscriptionEventHandler lndSubscriptionEventHandler) {
        this.lndClient = lndClient;
        this.lndSyncClient = lndSyncClient;
        this.lndSubscriptionEventHandler = lndSubscriptionEventHandler;
        this.executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public void startListening() {
        // Set up the initial state.
        lndSubscriptionEventHandler.handleInitialize();

        Log.i(getClass().getName(),"Starting to listen to subscription events.");
        startListenPeerEvents();
        startListenChannelEvents();
        startListenTransactions();
    }

    public void stopListening() {
        executorService.shutdown();
    }

    /**
     * Handle a new peer event.
     * @param peerEvent
     */
    protected void handlePeerEvent(Rpc.PeerEvent peerEvent) {
        Log.i(getClass().getName(), "New PeerEvent");
        lndSubscriptionEventHandler.handlePeerEvent(peerEvent);
    }

    /**
     * Handle a new channel update.
     * @param channelEventUpdate
     */
    protected void handleChannelEvent(Rpc.ChannelEventUpdate channelEventUpdate) {
        Log.i(getClass().getName(), "New ChannelEventUpdate");
        lndSubscriptionEventHandler.handleChannelEventUpdate(channelEventUpdate);
    }

    /**
     * Handle a new transaction.
     * @param transaction
     */
    protected void handleTransaction(Rpc.Transaction transaction) {
        Log.i(getClass().getName(), "New transaction");
        lndSubscriptionEventHandler.handleTransaction(transaction);
    }

    /**
     * Listen for peer from the LND daemon.
     */
    private void startListenPeerEvents() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Keep the set updated with the results from the updates.
                try {
                    listenPeerEvents();
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void listenPeerEvents() throws InterruptedException, ExecutionException, TimeoutException {
        lndSyncClient.subscribePeerEvents(new LndClient.SubscribePeerEventsRecvStream() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get peer event update: " + e);
                System.exit(1);
            }

            @Override
            public void onUpdate(Rpc.PeerEvent update) {
                handlePeerEvent(update);
            }
        });
    }


    /**
     * Listen for channel events from the LND daemon.
     */
    private void startListenChannelEvents() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(),"Starting listenChannelEvents thread.");
                // Keep the set updated with the results from the updates.
                try {
                    listenChannelEvents();
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void listenChannelEvents() throws InterruptedException, ExecutionException, TimeoutException {
        // TODO
    }

    /**
     * Listen for transactinos from the LND daemon.
     */
    private void startListenTransactions() {
        int startHeight = 0;
        int endHeight = -1;

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(),"Starting listenTransactions thread.");
                // Keep the set updated with the results from the updates.
                try {
                    listenTransactions();
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void listenTransactions() throws InterruptedException, ExecutionException, TimeoutException {
        // TODO
    }

}
