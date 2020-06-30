package io.github.yzernik.squeakand.lnd;

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
import java.util.concurrent.TimeoutException;

import io.github.yzernik.squeakand.DataResult;
import lnrpc.Rpc;

public class LndLiveDataClient {

    private LndSyncClient lndSyncClient;
    private ExecutorService executorService;

    public LndLiveDataClient(LndSyncClient lndSyncClient, ExecutorService executorService) {
        this.executorService = executorService;
        this.lndSyncClient = lndSyncClient;
    }

    public LiveData<DataResult<Rpc.GetInfoResponse>> getInfo() {
        MutableLiveData<DataResult<Rpc.GetInfoResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.GetInfoResponse response = lndSyncClient.getInfo();
                    liveDataResult.postValue(DataResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(DataResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<DataResult<Rpc.WalletBalanceResponse>> walletBalance() {
        MutableLiveData<DataResult<Rpc.WalletBalanceResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.WalletBalanceResponse response = lndSyncClient.walletBalance();
                    liveDataResult.postValue(DataResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(DataResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<DataResult<Rpc.ListChannelsResponse>> listChannels() {
        MutableLiveData<DataResult<Rpc.ListChannelsResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.ListChannelsResponse response = lndSyncClient.listChannels();
                    liveDataResult.postValue(DataResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(DataResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<DataResult<Rpc.NewAddressResponse>> newAddress() {
        MutableLiveData<DataResult<Rpc.NewAddressResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.NewAddressResponse response = lndSyncClient.newAddress();
                    liveDataResult.postValue(DataResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(DataResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<DataResult<Rpc.SendResponse>> sendPayment(String paymentRequest) {
        MutableLiveData<DataResult<Rpc.SendResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.SendResponse response = lndSyncClient.sendPayment(paymentRequest);
                    liveDataResult.postValue(DataResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(DataResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }


    public LiveData<DataResult<Rpc.ConnectPeerResponse>> connectPeer(String pubkey, String host) {
        MutableLiveData<DataResult<Rpc.ConnectPeerResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.ConnectPeerResponse response = lndSyncClient.connectPeer(pubkey, host);
                    liveDataResult.postValue(DataResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(DataResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<DataResult<Rpc.ListPeersResponse>> listPeers() {
        MutableLiveData<DataResult<Rpc.ListPeersResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.ListPeersResponse response = lndSyncClient.listPeers();
                    liveDataResult.postValue(DataResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(DataResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<DataResult<Rpc.ChannelPoint>> openChannel(String pubkey, long amount) {
        MutableLiveData<DataResult<Rpc.ChannelPoint>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.ChannelPoint response = lndSyncClient.openChannel(pubkey, amount);
                    liveDataResult.postValue(DataResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(DataResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
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

}
