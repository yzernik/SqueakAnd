package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import io.github.yzernik.squeakand.DataResult;
import lnrpc.Rpc;

public class LndLiveDataClient implements LndController.LndControllerUpdateHandler, LndSubscriptionEventHandler {

    private LndSyncClient lndSyncClient;
    private ExecutorService executorService;
    private MutableLiveData<LndWalletStatus> liveLndWalletStatus;
    private MutableLiveData<Rpc.GetInfoResponse> liveGetInfoResponse;
    private MutableLiveData<Rpc.WalletBalanceResponse> liveWalletBalanceResponse;
    private MutableLiveData<Rpc.ListChannelsResponse> liveListChannelsResponse;
    private MutableLiveData<Rpc.PendingChannelsResponse> livePendingChannelsResponse;

    public LndLiveDataClient(LndSyncClient lndSyncClient, ExecutorService executorService) {
        this.executorService = executorService;
        this.lndSyncClient = lndSyncClient;
        this.liveLndWalletStatus = new MutableLiveData<>();
        this.liveGetInfoResponse = new MutableLiveData<>();
        this.liveWalletBalanceResponse = new MutableLiveData<>();
        this.liveListChannelsResponse = new MutableLiveData<>();
        this.livePendingChannelsResponse = new MutableLiveData<>();
    }

    public LiveData<LndWalletStatus> getLndWalletStatus() {
        return liveLndWalletStatus;
    }

    public LiveData<Rpc.GetInfoResponse> getLiveGetInfo() {
        return liveGetInfoResponse;
    }

    public LiveData<Rpc.WalletBalanceResponse> getLiveWalletBalance() {
        return liveWalletBalanceResponse;
    }

    public LiveData<Rpc.ListChannelsResponse> getLiveChannels() {
        return liveListChannelsResponse;
    }

    public LiveData<Rpc.PendingChannelsResponse> getLivePendingChannels() {
        return livePendingChannelsResponse;
    }

    public LiveData<DataResult<Rpc.TransactionDetails>> getTransactions(int startHeight, int endHeight) {
        MutableLiveData<DataResult<Rpc.TransactionDetails>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.TransactionDetails response = lndSyncClient.getTransactions(startHeight, endHeight);
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

    private void setLiveGetInfo() {
        Log.i(getClass().getName(), "Getting GetInfo...");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.GetInfoResponse response = lndSyncClient.getInfo();
                    Log.i(getClass().getName(), "Got GetInfoResponse: " + response);
                    liveGetInfoResponse.postValue(response);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLiveWalletBalance() {
        Log.i(getClass().getName(), "Getting WalletBalance...");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.WalletBalanceResponse response = lndSyncClient.walletBalance();
                    Log.i(getClass().getName(), "Got WalletBalanceResponse: " + response);
                    liveWalletBalanceResponse.postValue(response);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLiveChannels() {
        Log.i(getClass().getName(), "Getting channels...");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.ListChannelsResponse response = lndSyncClient.listChannels();
                    Log.i(getClass().getName(), "Got listChannelsResponse with size: " + response.getChannelsList().size());
                    liveListChannelsResponse.postValue(response);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLivePendingChannels() {
        Log.i(getClass().getName(), "Getting pending channels...");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.PendingChannelsResponse response = lndSyncClient.pendingChannels();
                    Log.i(getClass().getName(), "Got PendingChannelsResponse: " + response);
                    livePendingChannelsResponse.postValue(response);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LiveData<DataResult<Rpc.CloseStatusUpdate>> closeChannel(Rpc.ChannelPoint channelPoint, boolean force) {
        Log.i(getClass().getName(), "Getting closeChannel...");
        MutableLiveData<DataResult<Rpc.CloseStatusUpdate>> liveCloseChannel = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                lndSyncClient.closeChannel(channelPoint, force, new LndClient.CloseChannelEventsRecvStream() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(getClass().getName(), "Failed to get close channel update: " + e);
                        liveCloseChannel.postValue(DataResult.ofFailure(e));
                    }

                    @Override
                    public void onUpdate(Rpc.CloseStatusUpdate update) {
                        liveCloseChannel.postValue(DataResult.ofSuccess(update));
                    }
                });
            }
        });
        return liveCloseChannel;
    }

    @Override
    public void setWalletStatus(LndWalletStatus status) {
        liveLndWalletStatus.postValue(status);
    }

    @Override
    public void handleChannelEventUpdate(Rpc.ChannelEventUpdate channelEventUpdate) {
        setLiveGetInfo();
        setLiveWalletBalance();
        setLiveChannels();
        setLivePendingChannels();
    }

    @Override
    public void handlePeerEvent(Rpc.PeerEvent peerEvent) {
        setLiveGetInfo();
    }

    @Override
    public void handleInvoice(Rpc.Invoice invoice) {
        setLiveGetInfo();
        setLiveWalletBalance();
    }

    @Override
    public void handleTransaction(Rpc.Transaction transaction) {
        setLiveGetInfo();
        setLiveWalletBalance();
    }

    @Override
    public void handleChanBackupSnapshot(Rpc.ChanBackupSnapshot chanBackupSnapshot) {
        setLiveGetInfo();
    }

    @Override
    public void handleGraphTopologyUpdate(Rpc.GraphTopologyUpdate graphTopologyUpdate) {
        setLiveGetInfo();
    }

    @Override
    public void handleInitialize() {
        setLiveGetInfo();
        setLiveWalletBalance();
        setLiveChannels();
        setLivePendingChannels();
    }
}
