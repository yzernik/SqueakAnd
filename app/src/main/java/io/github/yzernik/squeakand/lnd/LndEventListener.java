package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lnrpc.Rpc;

public class LndEventListener {

    private static final int NUM_THREADS = 3;

    private LndSyncClient lndSyncClient;
    private LndSubscriptionEventHandler lndSubscriptionEventHandler;
    private ExecutorService executorService;

    public LndEventListener(LndSyncClient lndSyncClient, LndSubscriptionEventHandler lndSubscriptionEventHandler) {
        this.lndSyncClient = lndSyncClient;
        this.lndSubscriptionEventHandler = lndSubscriptionEventHandler;
        //this.executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public void startListening() {
        this.executorService = Executors.newFixedThreadPool(NUM_THREADS);

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
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void listenPeerEvents() throws InterruptedException, ExecutionException {
        lndSyncClient.subscribePeerEvents(new LndClient.SubscribePeerEventsRecvStream() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get peer event update: " + e);
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
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void listenChannelEvents() throws InterruptedException, ExecutionException {
        lndSyncClient.subscribeChannelEvents(new LndClient.SubscribeChannelEventsRecvStream() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get channel event update: " + e);
            }

            @Override
            public void onUpdate(Rpc.ChannelEventUpdate channelEventUpdate) {
                handleChannelEvent(channelEventUpdate);
            }
        });
    }

    /**
     * Listen for transactinos from the LND daemon.
     */
    private void startListenTransactions() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(),"Starting listenTransactions thread.");
                // Keep the set updated with the results from the updates.
                try {
                    listenTransactions();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void listenTransactions() throws InterruptedException, ExecutionException {
        int startHeight = 0;
        int endHeight = -1;

        lndSyncClient.subscribeTransactions(startHeight, endHeight, new LndClient.SubscribeTransactionsRecvStream() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get transaction: " + e);
            }

            @Override
            public void onUpdate(Rpc.Transaction transaction) {
                handleTransaction(transaction);
            }
        });
    }

}
