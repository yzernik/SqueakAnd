package io.github.yzernik.squeakand;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.yzernik.squeakand.preferences.Preferences;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PreferencesTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    Preferences preferences;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        preferences = new Preferences(context);
    }

    @After
    public void closeDb() {
        // Do nothing.
    }

    @Test
    public void testInsertAndGetSelectedProfileId() throws Exception {
        int profileId = 567;

        preferences.saveSelectedProfileId(profileId);

        assertEquals(profileId, preferences.getProfileId());
    }

}
