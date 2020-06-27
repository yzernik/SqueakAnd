package io.github.yzernik.squeakand.lnd;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import lnrpc.Rpc;
import lnrpc.Walletunlocker;

public class LndRepository {

    private static volatile LndRepository INSTANCE;

    // private LndClient lndClient;
    private LndController lndController;
    private ExecutorService executorService;
    private LndAsyncClient lndAsyncClient;

    private LndRepository(Application application) {
        // Singleton constructor, only called by static method.
        this.lndController = new LndController(application, "testnet");
        this.executorService = Executors.newCachedThreadPool();
        this.lndAsyncClient = new LndAsyncClient(lndController);
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

    public LndController getLndController() {
        return lndController;
    }

    public LndAsyncClient getLndAsyncClient() {
        return lndAsyncClient;
    }

    public void initialize() {
        Log.i(getClass().getName(), "LndRepository: Calling initialize ...");
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                // Start the lnd node
                try {
                    String startResult = lndController.start();
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
                    Walletunlocker.UnlockWalletResponse unlockResult = lndController.unlockWallet();
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
                    String[] seedWords = lndController.genSeed();
                    Walletunlocker.InitWalletResponse initWalletResult = lndController.initWallet(seedWords);
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

    public LiveData<Rpc.GetInfoResponse> getInfo() {
        Log.i(getClass().getName(), "Getting info...");
        MutableLiveData<Rpc.GetInfoResponse> liveGetInfoResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Future<Rpc.GetInfoResponse> responseFuture = lndController.getInfoAsync();
                    Rpc.GetInfoResponse response = responseFuture.get();
                    liveGetInfoResponse.postValue(response);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveGetInfoResponse;
    }

    public LiveData<Rpc.WalletBalanceResponse> walletBalance() {
        Log.i(getClass().getName(), "Getting walletBalance...");
        MutableLiveData<Rpc.WalletBalanceResponse> liveWalletBalanceResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Future<Rpc.WalletBalanceResponse> responseFuture = lndController.walletBalanceAsync();
                    Rpc.WalletBalanceResponse response = responseFuture.get();
                    liveWalletBalanceResponse.postValue(response);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveWalletBalanceResponse;
    }

    public LiveData<Rpc.ListChannelsResponse> listChannels() {
        Log.i(getClass().getName(), "Getting listChannels...");
        MutableLiveData<Rpc.ListChannelsResponse> liveListChannelsResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Future<Rpc.ListChannelsResponse> responseFuture = lndController.listChannelsAsync();
                    Rpc.ListChannelsResponse response = responseFuture.get();
                    liveListChannelsResponse.postValue(response);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveListChannelsResponse;
    }

    public LiveData<Rpc.NewAddressResponse> newAddress() {
        Log.i(getClass().getName(), "Getting newAddress...");
        MutableLiveData<Rpc.NewAddressResponse> liveNewAddressResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Future<Rpc.NewAddressResponse> responseFuture = lndController.newAddressAsync();
                    Rpc.NewAddressResponse response = responseFuture.get();
                    liveNewAddressResponse.postValue(response);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveNewAddressResponse;
    }

    public LiveData<Rpc.SendResponse> sendPayment(String paymentRequest) {
        Log.i(getClass().getName(), "Getting sendResponse...");
        MutableLiveData<Rpc.SendResponse> liveSendResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Future<Rpc.SendResponse> responseFuture = lndController.sendPaymentAsync(paymentRequest);
                    Rpc.SendResponse response = responseFuture.get();
                    liveSendResponse.postValue(response);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveSendResponse;
    }


    public LiveData<Rpc.ConnectPeerResponse> connectPeer(String pubkey, String host) {
        Log.i(getClass().getName(), "Getting connectPeer...");
        MutableLiveData<Rpc.ConnectPeerResponse> liveConnectPeerResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Future<Rpc.ConnectPeerResponse> responseFuture = lndController.connectPeerAsync(pubkey, host);
                    Rpc.ConnectPeerResponse response = responseFuture.get();
                    liveConnectPeerResponse.postValue(response);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveConnectPeerResponse;
    }

    public LiveData<Rpc.ListPeersResponse> listPeers() {
        Log.i(getClass().getName(), "Getting listPeers...");
        MutableLiveData<Rpc.ListPeersResponse> liveListPeersResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Future<Rpc.ListPeersResponse> responseFuture = lndController.listPeersAsync();
                    Rpc.ListPeersResponse response = responseFuture.get();
                    liveListPeersResponse.postValue(response);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveListPeersResponse;
    }

    public LiveData<Rpc.ChannelPoint> openChannel(String pubkey, long amount) {
        Log.i(getClass().getName(), "Getting openChannel...");
        MutableLiveData<Rpc.ChannelPoint> liveOpenChannelResponse = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Future<Rpc.ChannelPoint> responseFuture = lndController.openChannelAsync(pubkey, amount);
                    Rpc.ChannelPoint response = responseFuture.get();
                    liveOpenChannelResponse.postValue(response);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveOpenChannelResponse;
    }

    public LiveData<Rpc.ChannelEventUpdate> subscribeChannelEvents() {
        Log.i(getClass().getName(), "Getting subscribeChannelEvents...");
        MutableLiveData<Rpc.ChannelEventUpdate> liveChannelEvents = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                lndController.subscribeChannelEvents(new LndClient.SubscribeChannelEventsRecvStream() {
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
                lndController.closeChannel(channelPoint, force, new LndClient.CloseChannelEventsRecvStream() {
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
