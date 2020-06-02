package io.github.yzernik.squeakand;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.params.MainNetParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import io.github.yzernik.squeaklib.core.Signing;

import static io.github.yzernik.squeakand.SqueakRoomDatabase.MIGRATION_1_2;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static final Signing.BitcoinjKeyPair EXAMPLE_KEY_PAIR = new Signing.BitcoinjKeyPair();
    private static final String EXAMPLE_ADDRESS = EXAMPLE_KEY_PAIR.getPublicKey().getAddress(MainNetParams.get());

    private static final String TEST_DB_NAME = "test-db";

    private static final SqueakProfile SQUEAK_PROFILE = new SqueakProfile("alice", EXAMPLE_KEY_PAIR);

    // Helper for creating Room databases and migrations
    @Rule
    public MigrationTestHelper mMigrationTestHelper =
            new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                    SqueakRoomDatabase.class.getCanonicalName(),
                    new FrameworkSQLiteOpenHelperFactory());

    @Before
    public void setUp() throws Exception {
        // Nothing.
    }

    @After
    public void tearDown() throws Exception {
        // Nothing.
    }

    @Test
    public void migrationFrom1To2_containsCorrectData() throws IOException, InterruptedException {
        // Create the database with version 2
        SupportSQLiteDatabase db = mMigrationTestHelper.createDatabase(TEST_DB_NAME, 2);
        // Insert some data
        insertUser(SQUEAK_PROFILE.getName(), SQUEAK_PROFILE.getKeyPair(), db);
        //Prepare for the next version
        db.close();

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        mMigrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 2, true,
                MIGRATION_1_2);

        // MigrationTestHelper automatically verifies the schema changes, but not the data validity
        // Validate that the data was migrated properly.
        List<SqueakProfile> allProfiles = LiveDataTestUtil.getValue(getMigratedRoomDatabase().squeakProfileDao().getProfiles());
        SqueakProfile firstProfile = allProfiles.get(0);

        assertEquals(firstProfile.getName(), "alice");
        assertEquals(firstProfile.getAddress(), EXAMPLE_ADDRESS);
    }

    private void insertUser(String profileName, Signing.BitcoinjKeyPair keyPair, SupportSQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("name", profileName);
        values.put("keyPair", Converters.keyToString(keyPair));
        values.put("address", EXAMPLE_ADDRESS);

        db.insert("profile", SQLiteDatabase.CONFLICT_REPLACE, values);
    }

    private SqueakRoomDatabase getMigratedRoomDatabase() {
        SqueakRoomDatabase database = Room.databaseBuilder(ApplicationProvider.getApplicationContext(),
                SqueakRoomDatabase.class, TEST_DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .build();
        // close the database and release any stream resources when the test finishes
        mMigrationTestHelper.closeWhenFinished(database);
        return database;
    }

}
