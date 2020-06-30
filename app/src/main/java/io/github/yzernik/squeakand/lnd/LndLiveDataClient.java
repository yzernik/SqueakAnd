package io.github.yzernik.squeakand.lnd;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import lnrpc.Rpc;

public class LndLiveDataClient {

    private LndSyncClient lndSyncClient;
    private ExecutorService executorService;

    public LndLiveDataClient(LndSyncClient lndSyncClient, ExecutorService executorService) {
        this.executorService = executorService;
        this.lndSyncClient = lndSyncClient;
    }

    public LiveData<LndResult<Rpc.GetInfoResponse>> getInfo() {
        MutableLiveData<LndResult<Rpc.GetInfoResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.GetInfoResponse response = lndSyncClient.getInfo();
                    liveDataResult.postValue(LndResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(LndResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<LndResult<Rpc.WalletBalanceResponse>> walletBalance() {
        MutableLiveData<LndResult<Rpc.WalletBalanceResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.WalletBalanceResponse response = lndSyncClient.walletBalance();
                    liveDataResult.postValue(LndResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(LndResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<LndResult<Rpc.ListChannelsResponse>> listChannels() {
        MutableLiveData<LndResult<Rpc.ListChannelsResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.ListChannelsResponse response = lndSyncClient.listChannels();
                    liveDataResult.postValue(LndResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(LndResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<LndResult<Rpc.NewAddressResponse>> newAddress() {
        MutableLiveData<LndResult<Rpc.NewAddressResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.NewAddressResponse response = lndSyncClient.newAddress();
                    liveDataResult.postValue(LndResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(LndResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<LndResult<Rpc.SendResponse>> sendPayment(String paymentRequest) {
        MutableLiveData<LndResult<Rpc.SendResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.SendResponse response = lndSyncClient.sendPayment(paymentRequest);
                    liveDataResult.postValue(LndResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(LndResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }


    public LiveData<LndResult<Rpc.ConnectPeerResponse>> connectPeer(String pubkey, String host) {
        MutableLiveData<LndResult<Rpc.ConnectPeerResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.ConnectPeerResponse response = lndSyncClient.connectPeer(pubkey, host);
                    liveDataResult.postValue(LndResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(LndResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<LndResult<Rpc.ListPeersResponse>> listPeers() {
        MutableLiveData<LndResult<Rpc.ListPeersResponse>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.ListPeersResponse response = lndSyncClient.listPeers();
                    liveDataResult.postValue(LndResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(LndResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }

    public LiveData<LndResult<Rpc.ChannelPoint>> openChannel(String pubkey, long amount) {
        MutableLiveData<LndResult<Rpc.ChannelPoint>> liveDataResult = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Rpc.ChannelPoint response = lndSyncClient.openChannel(pubkey, amount);
                    liveDataResult.postValue(LndResult.ofSuccess(response));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                    liveDataResult.postValue(LndResult.ofFailure(e));
                }
            }
        });
        return liveDataResult;
    }


}
