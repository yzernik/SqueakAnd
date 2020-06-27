package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lndmobile.Callback;
import lndmobile.Lndmobile;
import lndmobile.RecvStream;
import lnrpc.Rpc;
import lnrpc.Walletunlocker;

public class LndClient {

    private static final String DEFAULT_NETWORK = "testnet";

    private final ExecutorService executorService;

    public LndClient() {
        this.executorService = Executors.newCachedThreadPool();
    }

    public void start(String lndDirPath, String network, StartCallBack callBack) {
        String startString = String.format(
                "--bitcoin.active --bitcoin.node=neutrino --bitcoin.%s --no-macaroons --lnddir=%s",
                network,
                lndDirPath);

        Log.i(getClass().getName(), "Starting lndmobile with lndDir: " + lndDirPath);
        Log.i(getClass().getName(), "Start string : " + startString);
        Lndmobile.start(
                startString,
                new Callback() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(getClass().getName(), "Error from callback1: " + e);
                        callBack.onError1(e);
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        Log.i(getClass().getName(), "Response from callback1: " + bytes);
                        callBack.onResponse1();
                    }
                },
                new Callback() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(getClass().getName(), "Error from callback1: " + e);
                        callBack.onError2(e);
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        Log.i(getClass().getName(), "Response from callback2: " + bytes);
                        callBack.onResponse2();
                    }
                });
    }

    public interface StartCallBack {
        public void onError1(Exception e);
        public void onResponse1();
        public void onError2(Exception e);
        public void onResponse2();
    }

    public void stop(StopCallBack callBack) {
        Rpc.StopRequest request = Rpc.StopRequest.newBuilder().build();
        Lndmobile.stopDaemon(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from stopDaemon callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    Rpc.StopResponse resp = Rpc.StopResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got stopDaemon response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface StopCallBack {
        public void onError(Exception e);
        public void onResponse(Rpc.StopResponse response);
    }

    public void initWallet(String password, List<String> seedWords, InitWalletCallBack callBack) {
        ByteString pw = ByteString.copyFromUtf8(password);
        Walletunlocker.InitWalletRequest request = Walletunlocker.InitWalletRequest.newBuilder()
                .setWalletPassword(pw)
                .addAllCipherSeedMnemonic(seedWords)
                .build();

        Lndmobile.initWallet(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from initWallet callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                Log.i(getClass().getName(), "Got initWallet response bytes: " + bytes);
                Walletunlocker.InitWalletResponse response = Walletunlocker.InitWalletResponse.getDefaultInstance();
                callBack.onResponse(response);
            }
        });
    }

    public interface InitWalletCallBack {
        public void onError(Exception e);
        public void onResponse(Walletunlocker.InitWalletResponse response);
    }

    public void genSeed(GenSeedCallBack callBack) {
        Walletunlocker.GenSeedRequest request = Walletunlocker.GenSeedRequest.newBuilder()
                .build();

        Lndmobile.genSeed(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from genSeed callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    Walletunlocker.GenSeedResponse resp = Walletunlocker.GenSeedResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got genseed response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface GenSeedCallBack {
        public void onError(Exception e);
        public void onResponse(Walletunlocker.GenSeedResponse response);
    }

    public void unlockWallet(String password, UnlockWalletCallBack callBack) {
        ByteString pw = ByteString.copyFromUtf8(password);
        Walletunlocker.UnlockWalletRequest request = Walletunlocker.UnlockWalletRequest.newBuilder()
                .setWalletPassword(pw)
                .build();

        Lndmobile.unlockWallet(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from unlockWallet callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                Log.i(getClass().getName(), "Got unlockWallet response bytes: " + bytes);
                Walletunlocker.UnlockWalletResponse response = Walletunlocker.UnlockWalletResponse.getDefaultInstance();
                callBack.onResponse(response);
            }
        });
    }

    public interface UnlockWalletCallBack {
        public void onError(Exception e);
        public void onResponse(Walletunlocker.UnlockWalletResponse response);
    }

    public void getInfo(GetInfoCallBack callBack) {
        Rpc.GetInfoRequest request = Rpc.GetInfoRequest.newBuilder().build();
        Lndmobile.getInfo(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from getInfo callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    Rpc.GetInfoResponse resp = Rpc.GetInfoResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got getInfo response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface GetInfoCallBack {
        public void onError(Exception e);
        public void onResponse(Rpc.GetInfoResponse response);
    }

    public void walletBalance(WalletBalanceCallBack callBack) {
        Rpc.WalletBalanceRequest request = Rpc.WalletBalanceRequest.newBuilder().build();
        Lndmobile.walletBalance(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from walletBalance callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    Rpc.WalletBalanceResponse resp = Rpc.WalletBalanceResponse.getDefaultInstance();
                    Log.i(getClass().getName(), "Got walletBalance response: " + resp);
                    callBack.onResponse(resp);
                    return;
                }

                try {
                    Rpc.WalletBalanceResponse resp = Rpc.WalletBalanceResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got walletBalance response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface WalletBalanceCallBack {
        public void onError(Exception e);
        public void onResponse(Rpc.WalletBalanceResponse response);
    }

    public void listChannels(ListChannelsCallBack callBack) {
        Rpc.ListChannelsRequest request = Rpc.ListChannelsRequest.newBuilder()
                .setActiveOnly(false)
                .setInactiveOnly(false)
                .setPublicOnly(false)
                .setPrivateOnly(false)
                .build();
        Lndmobile.listChannels(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from listChannels callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    Rpc.ListChannelsResponse resp = Rpc.ListChannelsResponse.getDefaultInstance();
                    Log.i(getClass().getName(), "Got listChannels response: " + resp);
                    callBack.onResponse(resp);
                    return;
                }

                try {
                    Rpc.ListChannelsResponse resp = Rpc.ListChannelsResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got listChannels response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface ListChannelsCallBack {
        public void onError(Exception e);
        public void onResponse(Rpc.ListChannelsResponse response);
    }

    public void newAddress(NewAddressCallBack callBack) {
        Rpc.AddressType addressType = Rpc.AddressType.WITNESS_PUBKEY_HASH;
        Rpc.NewAddressRequest request = Rpc.NewAddressRequest.newBuilder()
                .setType(addressType)
                .build();
        Lndmobile.newAddress(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from newAddress callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    Rpc.NewAddressResponse resp = Rpc.NewAddressResponse.getDefaultInstance();
                    Log.i(getClass().getName(), "Got walletBalance response: " + resp);
                    callBack.onResponse(resp);
                    return;
                }

                try {
                    Rpc.NewAddressResponse resp = Rpc.NewAddressResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got newAddress response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface NewAddressCallBack {
        public void onError(Exception e);
        public void onResponse(Rpc.NewAddressResponse response);
    }

    public void sendPayment(String paymentRequest, SendPaymentCallBack callBack) {
        Rpc.SendRequest request = Rpc.SendRequest.newBuilder()
                .setPaymentRequest(paymentRequest)
                .build();
        Lndmobile.sendPaymentSync(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from sendPayment callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    Rpc.SendResponse resp = Rpc.SendResponse.getDefaultInstance();
                    Log.i(getClass().getName(), "Got SendResponse response: " + resp);
                    callBack.onResponse(resp);
                    return;
                }

                try {
                    Rpc.SendResponse resp = Rpc.SendResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got sendPayment response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface SendPaymentCallBack {
        public void onError(Exception e);
        public void onResponse(Rpc.SendResponse response);
    }

    public void connectPeer(String pubkey, String host, ConnectPeerCallBack callBack) {
        Rpc.LightningAddress lightningAddress = Rpc.LightningAddress.newBuilder()
                .setPubkey(pubkey)
                .setHost(host)
                .build();
        Rpc.ConnectPeerRequest request = Rpc.ConnectPeerRequest.newBuilder()
                .setAddr(lightningAddress)
                .build();
        Lndmobile.connectPeer(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from connectPeer callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    Rpc.ConnectPeerResponse resp = Rpc.ConnectPeerResponse.getDefaultInstance();
                    Log.i(getClass().getName(), "Got ConnectPeerResponse response: " + resp);
                    callBack.onResponse(resp);
                    return;
                }

                try {
                    Rpc.ConnectPeerResponse resp = Rpc.ConnectPeerResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got ConnectPeerResponse response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface ConnectPeerCallBack {
        public void onError(Exception e);
        public void onResponse(Rpc.ConnectPeerResponse response);
    }

    public void listPeers(ListPeersCallBack callBack) {
        Rpc.ListPeersRequest request = Rpc.ListPeersRequest.newBuilder()
                .setLatestError(true)
                .build();
        Lndmobile.listPeers(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from listPeers callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    Rpc.ListPeersResponse resp = Rpc.ListPeersResponse.getDefaultInstance();
                    Log.i(getClass().getName(), "Got listPeers response: " + resp);
                    callBack.onResponse(resp);
                    return;
                }

                try {
                    Rpc.ListPeersResponse resp = Rpc.ListPeersResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got listPeers response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface ListPeersCallBack {
        public void onError(Exception e);
        public void onResponse(Rpc.ListPeersResponse response);
    }

    public void openChannel(String pubkey, long amount, OpenChannelCallBack callBack) {
        Rpc.OpenChannelRequest request = Rpc.OpenChannelRequest.newBuilder()
                .setNodePubkeyString(pubkey)
                .setLocalFundingAmount(amount)
                .build();
        Lndmobile.openChannelSync(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from openChannelSync callback: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    Rpc.ChannelPoint resp = Rpc.ChannelPoint.getDefaultInstance();
                    Log.i(getClass().getName(), "Got ChannelPoint response: " + resp);
                    callBack.onResponse(resp);
                    return;
                }

                try {
                    Rpc.ChannelPoint resp = Rpc.ChannelPoint.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got ChannelPoint response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface OpenChannelCallBack {
        public void onError(Exception e);
        public void onResponse(Rpc.ChannelPoint response);
    }

    public void subscribeChannelEvents(SubscribeChannelEventsRecvStream callBack) {
        Rpc.ChannelEventSubscription request = Rpc.ChannelEventSubscription.newBuilder().build();
        Lndmobile.subscribeChannelEvents(request.toByteArray(), new RecvStream() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from subscribeChannelEvents RecvStream: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    Rpc.ChannelEventUpdate update = Rpc.ChannelEventUpdate.getDefaultInstance();
                    Log.i(getClass().getName(), "Got ChannelEventUpdate update: " + update);
                    callBack.onUpdate(update);
                    return;
                }

                try {
                    Rpc.ChannelEventUpdate update = Rpc.ChannelEventUpdate.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got ChannelPoint update: " + update);
                    callBack.onUpdate(update);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface SubscribeChannelEventsRecvStream {
        public void onError(Exception e);
        public void onUpdate(Rpc.ChannelEventUpdate update);
    }

    public void closeChannel(Rpc.ChannelPoint channelPoint, boolean force, CloseChannelEventsRecvStream callBack) {
        Rpc.CloseChannelRequest request = Rpc.CloseChannelRequest.newBuilder()
                .setChannelPoint(channelPoint)
                .setForce(force)
                .build();
        Lndmobile.closeChannel(request.toByteArray(), new RecvStream() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from closeChannel RecvStream: " + e);
                callBack.onError(e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    Rpc.ClosedChannelUpdate update = Rpc.ClosedChannelUpdate.getDefaultInstance();
                    Log.i(getClass().getName(), "Got ClosedChannelUpdate update: " + update);
                    callBack.onUpdate(update);
                    return;
                }

                try {
                    Rpc.ClosedChannelUpdate update = Rpc.ClosedChannelUpdate.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got ClosedChannelUpdate update: " + update);
                    callBack.onUpdate(update);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface CloseChannelEventsRecvStream {
        public void onError(Exception e);
        public void onUpdate(Rpc.ClosedChannelUpdate update);
    }

}
