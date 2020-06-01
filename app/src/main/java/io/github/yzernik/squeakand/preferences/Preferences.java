package io.github.yzernik.squeakand.preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String SQUEAK_PROFILE_FILE_KEY = "io.github.yzernik.squeakand.SQUEAK_PREFERENCES";
    private static final String SELECTED_SQUEAK_PROFILE_ID_KEY = "SELECTED_SQUEAK_PROFILE_ID";

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

}
