package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import lnrpc.Rpc;

public interface LndSubscriptionEventHandler {

    /**
     * Handle a new channel event update.
     * @param channelEventUpdate
     */
    void handleChannelEventUpdate(Rpc.ChannelEventUpdate channelEventUpdate);

    /**
     * Handle a new peer event.
     * @param peerEvent
     */
    void handlePeerEvent(Rpc.PeerEvent peerEvent);

    /**
     * Handle a new invoice.
     * @param invoice
     */
    void handleInvoice(Rpc.Invoice invoice);

    /**
     * Handle a new transaction.
     * @param transaction
     */
    void handleTransaction(Rpc.Transaction transaction);

    /**
     * Handle a new channel backup snapshot.
     * @param chanBackupSnapshot
     */
    void handleChanBackupSnapshot(Rpc.ChanBackupSnapshot chanBackupSnapshot);


    /**
     * Handle a new graph topology update.
     * @param graphTopologyUpdate
     */
    void handleGraphTopologyUpdate(Rpc.GraphTopologyUpdate graphTopologyUpdate);

    /**
     * Set up the initial state.
     */
    void handleInitialize();

}
