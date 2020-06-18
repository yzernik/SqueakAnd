package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lndmobile.Callback;
import lndmobile.Lndmobile;
import lnrpc.Rpc;
import lnrpc.Walletunlocker;

public class LndClient {

    private static final String DEFAULT_NETWORK = "testnet";

    private final ExecutorService executorService;

    public LndClient() {
        this.executorService = Executors.newCachedThreadPool();
    }

    public void start(String lndDirPath, String network) {
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
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        Log.i(getClass().getName(), "Response from callback1: " + bytes);
                    }
                },
                new Callback() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(getClass().getName(), "Error from callback1: " + e);
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        Log.i(getClass().getName(), "Response from callback2: " + bytes);
                    }
                });
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
                    callBack.onError(new Exception("Null response."));
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

}
