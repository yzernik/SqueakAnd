package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import lndmobile.Callback;
import lndmobile.Lndmobile;

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
        String dataDir = lndDirPath + "/data";
        String logDir = lndDirPath + "/logs";
        String tlsCert = lndDirPath + "/tls.cert";
        String tlsKey = lndDirPath + "/tls.key";

        String startString = String.format(
                "--bitcoin.active --bitcoin.node=neutrino --bitcoin.%s --no-macaroons --datadir=%s --logdir=%s --tlscertpath=%s --tlskeypath=%s",
                network,
                dataDir,
                logDir,
                tlsCert,
                tlsKey);

        Log.i(getClass().getName(), "Starting lndmobile with lndDir: " + lndDirPath);
        Log.i(getClass().getName(), "Start string : " + startString);
        Lndmobile.start(
                "--bitcoin.active --bitcoin.node=neutrino --bitcoin.testnet --no-macaroons",
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

}
