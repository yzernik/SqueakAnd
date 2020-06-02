package io.github.yzernik.squeakand.preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;

public class Preferences {

    private static final String SQUEAK_PROFILE_FILE_KEY = "io.github.yzernik.squeakand.SQUEAK_PREFERENCES";
    private static final String SELECTED_SQUEAK_PROFILE_ID_KEY = "SELECTED_SQUEAK_PROFILE_ID";
    private static final String ELECTRUM_SERVER_HOST_KEY = "ELECTRUM_SERVER_HOST";
    private static final String ELECTRUM_SERVER_PORT_KEY = "ELECTRUM_SERVER_PORT";

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

}