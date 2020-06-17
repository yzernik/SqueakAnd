package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lndmobile.Callback;
import lndmobile.Lndmobile;
import lnrpc.Lnd;
import lnrpc.Walletunlocker;

import static org.bitcoinj.core.Utils.HEX;

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

    public void getInfo() {
        // TODO: use a callback that returns a deserialized getinfo response.

        Lnd.GetInfoRequest request = Lnd.GetInfoRequest.newBuilder()
                .build();


        Lndmobile.getInfo(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from getInfo callback: " + e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                Log.i(getClass().getName(), "Response from getInfo callback: " + bytes);
            }
        });

        // Lndmobile.getInfo();
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
                try {
                    Log.i(getClass().getName(), "Got response bytes: " + bytes);
                    Log.i(getClass().getName(), "Got response encoded bytes: " + HEX.encode(bytes));
                    Walletunlocker.InitWalletResponse resp = Walletunlocker.InitWalletResponse.parseFrom(bytes);
                    Log.i(getClass().getName(), "Got initWallet response: " + resp);
                    callBack.onResponse(resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
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

}
