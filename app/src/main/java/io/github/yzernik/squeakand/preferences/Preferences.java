package io.github.yzernik.squeakand.preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;

public class Preferences {

    private static final String SQUEAK_PROFILE_FILE_KEY = "io.github.yzernik.squeakand.SQUEAK_PREFERENCES";
    private static final String SELECTED_SQUEAK_PROFILE_ID_KEY = "SELECTED_SQUEAK_PROFILE_ID";
    private static final String ELECTRUM_SERVER_HOST_KEY = "ELECTRUM_SERVER_HOST";
    private static final String ELECTRUM_SERVER_PORT_KEY = "ELECTRUM_SERVER_PORT";
    private static final String LND_WALLET_SEED_KEY = "LND_WALLET_SEED";

    private SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences(
                SQUEAK_PROFILE_FILE_KEY, Context.MODE_PRIVATE);
    }

    public int getProfileId() {
        return sharedPreferences.getInt(SELECTED_SQUEAK_PROFILE_ID_KEY, -1);
    }

    public void saveSelectedProfileId(int squeakProfileId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SELECTED_SQUEAK_PROFILE_ID_KEY, squeakProfileId);
        editor.commit();
    }

    public ElectrumServerAddress getElectrumServerAddress() {
        String host = sharedPreferences.getString(ELECTRUM_SERVER_HOST_KEY, "");
        int port = sharedPreferences.getInt(ELECTRUM_SERVER_PORT_KEY, -1);
        if (host.equals("") || port == -1) {
            return null;
        }
        return new ElectrumServerAddress(host, port);
    }

    public void saveElectrumServerAddress(ElectrumServerAddress serverAddress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ELECTRUM_SERVER_HOST_KEY, serverAddress.getHost());
        editor.putInt(ELECTRUM_SERVER_PORT_KEY, serverAddress.getPort());
        editor.commit();
    }

    public String[] getWalletSeed() {
        String seedString = sharedPreferences.getString(LND_WALLET_SEED_KEY, "");
        if (seedString.isEmpty()) {
            return null;
        }
        return seedString.split(",");
    }

    public void saveWalletSeed(String[] seed) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String seedString = String.join(",", seed);
        editor.putString(LND_WALLET_SEED_KEY, seedString);
        editor.commit();
    }

    public void clearWalletSeed() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(LND_WALLET_SEED_KEY);
        editor.commit();
    }

}
