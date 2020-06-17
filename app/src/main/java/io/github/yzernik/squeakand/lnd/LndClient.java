package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Arrays;

import lndmobile.Callback;
import lndmobile.Lndmobile;
import lnrpc.Lnd;
import lnrpc.Walletunlocker;

import static org.bitcoinj.core.Utils.HEX;

public class LndClient {

    private static final String DEFAULT_NETWORK = "testnet";

    private final String network;
    private final String lndDirPath;

    public LndClient(String lndDirPath, String network) {
        this.lndDirPath = lndDirPath;
        this.network = network;
    }

    public LndClient(String lndDirPath) {
        this(lndDirPath, DEFAULT_NETWORK);
    }

    public void start() {
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
                        Log.i(getClass().getName(), "Response from callback1: " + HEX.encode(bytes));
                    }
                },
                new Callback() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(getClass().getName(), "Error from callback1: " + e);
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        Log.i(getClass().getName(), "Response from callback2: " + HEX.encode(bytes));
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
                Log.i(getClass().getName(), "Response from getInfo callback: " + HEX.encode(bytes));
            }
        });

        // Lndmobile.getInfo();
    }

    public void initWallet() {
        ByteString pw = ByteString.copyFromUtf8("somesuperstrongpw");
        String[] cipherSeed = new String[]{
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "18",
                "19",
                "20",
                "21",
                "22",
                "23",
                "24"};
        Walletunlocker.InitWalletRequest request = Walletunlocker.InitWalletRequest.newBuilder()
                .setWalletPassword(pw)
                .addAllCipherSeedMnemonic(Arrays.asList(cipherSeed))
                .build();

        Lndmobile.initWallet(request.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Error from initWallet callback: " + e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    Walletunlocker.InitWalletResponse resp = Walletunlocker.InitWalletResponse.parseFrom(bytes);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
