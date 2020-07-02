package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import java.util.concurrent.ExecutorService;

import lnrpc.Rpc;

public class LndEventListener {

    private LndClient lndClient;
    private LndSubscriptionEventHandler lndSubscriptionEventHandler;
    private ExecutorService executorService;

    public LndEventListener(LndClient lndClient, LndSubscriptionEventHandler lndSubscriptionEventHandler, ExecutorService executorService) {
        this.lndClient = lndClient;
        this.lndSubscriptionEventHandler = lndSubscriptionEventHandler;
        this.executorService = executorService;
    }

    public void listenSubscriptionEvents() {
        // Set up the initial state.
        lndSubscriptionEventHandler.handleInitialize();

        Log.i(getClass().getName(),"Starting to listen to subscription events.");
        listenPeerEvents();
        listenChannelEvents();
    }

    /**
     * Listen for peer from the LND daemon.
     */
    private void listenPeerEvents() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Keep the set updated with the results from the updates.
                lndClient.subscribePeerEvents(new LndClient.SubscribePeerEventsRecvStream() {
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
        });
    }

    /**
     * Handle a new peer event.
     * @param peerEvent
     */
    protected void handlePeerEvent(Rpc.PeerEvent peerEvent) {
        Log.i(getClass().getName(), "New peer event");
        lndSubscriptionEventHandler.handlePeerEvent(peerEvent);
    }

    /**
     * Listen for channel events from the LND daemon.
     */
    private void listenChannelEvents() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(),"Starting listenChannelEvents thread.");
                // Keep the set updated with the results from the updates.
                lndClient.subscribeChannelEvents(new LndClient.SubscribeChannelEventsRecvStream() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(getClass().getName(), "Failed to get channel event update: " + e);
                        System.exit(1);
                    }

                    @Override
                    public void onUpdate(Rpc.ChannelEventUpdate update) {
                        handleChannelEvent(update);
                    }
                });

            }
        });
    }

    /**
     * Handle a new channel update.
     * @param channelEventUpdate
     */
    protected void handleChannelEvent(Rpc.ChannelEventUpdate channelEventUpdate) {
        Log.i(getClass().getName(), "New peer event");
        lndSubscriptionEventHandler.handleChannelEventUpdate(channelEventUpdate);
    }

}