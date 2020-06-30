package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import lnrpc.Rpc;
import lnrpc.Walletunlocker;

public class LndRepository {

    private static volatile LndRepository INSTANCE;

    // private LndClient lndClient;
    private LndSyncClient lndSyncClient;
    private ExecutorService executorService;
    private LndLiveDataClient lndLiveDataClient;

    private LndRepository(Application application) {
        // Singleton constructor, only called by static method.
        this.lndSyncClient = new LndSyncClient(application, "testnet");
        this.executorService = Executors.newCachedThreadPool();
        this.lndLiveDataClient = new LndLiveDataClient(lndSyncClient, executorService);
    }

    public static LndRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (LndRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LndRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public LndSyncClient getLndSyncClient() {
        return lndSyncClient;
    }

    public void initialize() {
        Log.i(getClass().getName(), "LndRepository: Calling initialize ...");
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                // Start the lnd node
                try {
                    String startResult = lndSyncClient.start();
                    Log.i(getClass().getName(), "Started node with result: " + startResult);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    Log.i(getClass().getName(), "Failed to start lnd node.");
                    System.exit(1);
                }

                // Unlock the wallet
                boolean walletUnlocked = false;

                // Unlock the existing wallet
                try {
                    Walletunlocker.UnlockWalletResponse unlockResult = lndSyncClient.unlockWallet();
                    Log.i(getClass().getName(), "Unlocked wallet with result: " + unlockResult);
                    walletUnlocked = true;
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    Log.i(getClass().getName(), "Failed to unlock wallet.");
                    // System.exit(1);
                }

                if (walletUnlocked) {
                    return;
                }

                // Create a new wallet
                try {
                    String[] seedWords = lndSyncClient.genSeed();
                    Walletunlocker.InitWalletResponse initWalletResult = lndSyncClient.initWallet(seedWords);
                    Log.i(getClass().getName(), "Initialized wallet with result: " + initWalletResult);
                    walletUnlocked = true;
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    Log.i(getClass().getName(), "Failed to initialize wallet.");
                }

                if (!walletUnlocked) {
                    System.exit(1);
                }

            }
        });
    }

    public LiveData<LndResult<Rpc.GetInfoResponse>> getInfo() {
        return lndLiveDataClient.getInfo();
    }

    public LiveData<LndResult<Rpc.WalletBalanceResponse>> walletBalance() {
        return lndLiveDataClient.walletBalance();
    }

    public LiveData<LndResult<Rpc.ListChannelsResponse>> listChannels() {
        return lndLiveDataClient.listChannels();
    }

    public LiveData<LndResult<Rpc.NewAddressResponse>> newAddress() {
        return lndLiveDataClient.newAddress();
    }

    public LiveData<LndResult<Rpc.SendResponse>> sendPayment(String paymentRequest) {
        return lndLiveDataClient.sendPayment(paymentRequest);
    }

    public LiveData<LndResult<Rpc.ConnectPeerResponse>> connectPeer(String pubkey, String host) {
        return lndLiveDataClient.connectPeer(pubkey, host);
    }

    public LiveData<LndResult<Rpc.ListPeersResponse>> listPeers() {
        return lndLiveDataClient.listPeers();
    }

    public LiveData<LndResult<Rpc.ChannelPoint>> openChannel(String pubkey, long amount) {
        return lndLiveDataClient.openChannel(pubkey, amount);
    }

    public LiveData<Rpc.ChannelEventUpdate> subscribeChannelEvents() {
        Log.i(getClass().getName(), "Getting subscribeChannelEvents...");
        MutableLiveData<Rpc.ChannelEventUpdate> liveChannelEvents = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                lndSyncClient.subscribeChannelEvents(new LndClient.SubscribeChannelEventsRecvStream() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(getClass().getName(), "Failed to get channel event: " + e);
                    }

                    @Override
                    public void onUpdate(Rpc.ChannelEventUpdate update) {
                        liveChannelEvents.postValue(update);
                    }
                });
            }
        });
        return liveChannelEvents;
    }

    public LiveData<Rpc.ClosedChannelUpdate> closeChannel(String channelPointString, boolean force) {
        String[] parts = channelPointString.split(":");
        String fundingTx = parts[0];
        int outputIndex = Integer.parseInt(parts[1]);

        Rpc.ChannelPoint channelPoint = Rpc.ChannelPoint.newBuilder()
                .setFundingTxidStr(fundingTx)
                .setOutputIndex(outputIndex)
                .build();
        return closeChannel(channelPoint, force);
    }

    public LiveData<Rpc.ClosedChannelUpdate> closeChannel(Rpc.ChannelPoint channelPoint, boolean force) {
        Log.i(getClass().getName(), "Getting closeChannel...");
        MutableLiveData<Rpc.ClosedChannelUpdate> liveCloseChannel = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                lndSyncClient.closeChannel(channelPoint, force, new LndClient.CloseChannelEventsRecvStream() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(getClass().getName(), "Failed to get close channel update: " + e);
                    }

                    @Override
                    public void onUpdate(Rpc.ClosedChannelUpdate update) {
                        liveCloseChannel.postValue(update);
                    }
                });
            }
        });
        return liveCloseChannel;
    }

    public LiveData<Set<String>> liveConnectedPeers() {
        Log.i(getClass().getName(), "Getting connected peers...");
        MutableLiveData<Set<String>> livePeers = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Rpc.ListPeersResponse listPeersResponse = lndSyncClient.listPeers();
                    Set<String> connectedPeers = new HashSet<>();

                    // Add the initial peers to the set.
                    for (Rpc.Peer peer: listPeersResponse.getPeersList()) {
                        connectedPeers.add(peer.getPubKey());
                    }
                    livePeers.postValue(connectedPeers);

                    // Keep the set updated with the results from the updates.
                    lndSyncClient.subscribePeerEvents(new LndClient.SubscribePeerEventsRecvStream() {
                        @Override
                        public void onError(Exception e) {
                            Log.e(getClass().getName(), "Failed to get peer event update: " + e);
                        }

                        @Override
                        public void onUpdate(Rpc.PeerEvent update) {
                            if (update.getType().equals(Rpc.PeerEvent.EventType.PEER_ONLINE)) {
                                connectedPeers.add(update.getPubKey());
                            } else {
                                connectedPeers.remove(update.getPubKey());
                            }
                            livePeers.postValue(connectedPeers);
                        }
                    });

                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
        return livePeers;
    }

    public LiveData<List<Rpc.Channel>> getLiveChannels() {
        Log.i(getClass().getName(), "Getting channels...");
        MutableLiveData<List<Rpc.Channel>> liveChannels = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Rpc.ListChannelsResponse listChannelsResponse = lndSyncClient.listChannels();
                    Log.i(getClass().getName(), "Got listChannelsResponse with size: " + listChannelsResponse.getChannelsList().size());
                    Map<String, Rpc.Channel> channels = new HashMap<>();

                    // Add the initial channels to the map.
                    for (Rpc.Channel channel: listChannelsResponse.getChannelsList()) {
                        channels.put(channel.getChannelPoint(), channel);
                    }
                    liveChannels.postValue(new ArrayList<>(channels.values()));

                    // Keep the set updated with the results from the updates.
                    lndSyncClient.subscribeChannelEvents(new LndClient.SubscribeChannelEventsRecvStream() {
                        @Override
                        public void onError(Exception e) {
                            Log.e(getClass().getName(), "Failed to get peer event update: " + e);
                        }

                        @Override
                        public void onUpdate(Rpc.ChannelEventUpdate update) {
                            if (update.getType().equals(Rpc.ChannelEventUpdate.UpdateType.OPEN_CHANNEL)) {
                                // Add the new channel to the map
                                Rpc.Channel channel = update.getOpenChannel();
                                channels.put(channel.getChannelPoint(), channel);
                            } else if (update.getType().equals(Rpc.ChannelEventUpdate.UpdateType.CLOSED_CHANNEL)) {
                                // Remove the existing channel from the map
                                Rpc.ChannelCloseSummary channelCloseSummary = update.getClosedChannel();
                                channels.remove(channelCloseSummary.getChannelPoint());
                            } else if (update.getType().equals(Rpc.ChannelEventUpdate.UpdateType.ACTIVE_CHANNEL)) {
                                // Replace the existing channel with a new one with active field set to true.
                                Rpc.ChannelPoint channelPoint = update.getActiveChannel();
                                String channelPointString = ChannelPointUtil.stringFromChannelPoint(channelPoint);
                                Rpc.Channel curChannel = channels.get(channelPointString);
                                Rpc.Channel newChannel = curChannel.toBuilder()
                                        .setActive(true)
                                        .build();
                                channels.put(newChannel.getChannelPoint(), newChannel);
                            } else if (update.getType().equals(Rpc.ChannelEventUpdate.UpdateType.INACTIVE_CHANNEL)) {
                                // Replace the existing channel with a new one with active field set to false.
                                Rpc.ChannelPoint channelPoint = update.getInactiveChannel();
                                String channelPointString = ChannelPointUtil.stringFromChannelPoint(channelPoint);
                                Rpc.Channel curChannel = channels.get(channelPointString);
                                Rpc.Channel newChannel = curChannel.toBuilder()
                                        .setActive(false)
                                        .build();
                                channels.put(newChannel.getChannelPoint(), newChannel);
                            }
                            liveChannels.postValue(new ArrayList<>(channels.values()));
                        }
                    });

                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveChannels;
    }

}
