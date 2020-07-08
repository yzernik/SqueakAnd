package io.github.yzernik.squeakand;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.  In a real
 * app, consider exporting the schema to help you with migrations.
 */

@Database(entities = {SqueakProfile.class, SqueakEntry.class, SqueakServer.class, Offer.class}, version = 14)
public abstract class SqueakRoomDatabase extends RoomDatabase {

    public static final String DB_NAME = "app_db";
    public static final String TABLE_NAME_PROFILE = "profile";
    public static final String TABLE_NAME_SQUEAK = "squeak";
    public static final String TABLE_NAME_SERVER = "server";
    public static final String TABLE_NAME_OFFER = "offer";

    abstract SqueakDao squeakDao();
    abstract SqueakProfileDao squeakProfileDao();
    abstract SqueakServerDao squeakServerDao();
    abstract OfferDao offerDao();


    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile SqueakRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static SqueakRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SqueakRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SqueakRoomDatabase.class, DB_NAME)
                            //.addCallback(sRoomDatabaseCallback)
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Override the onOpen method to populate the database.
     * For this sample, we clear the database every time it is created or opened.
     *
     * If you want to populate the database only when the database is created for the 1st time,
     * override RoomDatabase.Callback()#onCreate
     */
    /*
    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more todos, just add them.
                TodoDao dao = INSTANCE.todoDao();
                dao.deleteAll();

                for (Todo todo: INSTANCE.buildDummyTodos()) {
                    dao.insert(todo);
                }

                SqueakProfileDao squeakProfileDao = INSTANCE.squeakProfileDao();
                squeakProfileDao.deleteAll();
            });
        }
    };*/


    /*
    private List<Todo> buildDummyTodos() {
        List<Todo> todoArrayList = new ArrayList<>();
        Todo todo = new Todo();
        todo.name = "Android Retrofit Tutorial";
        todo.description = "Cover a tutorial on the Retrofit networking library using a RecyclerView to show the data.";
        todo.category = "Android";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "iOS TableView Tutorial";
        todo.description = "Covers the basics of TableViews in iOS using delegates.";
        todo.category = "iOS";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "Kotlin Arrays";
        todo.description = "Cover the concepts of Arrays in Kotlin and how they differ from the Java ones.";
        todo.category = "Kotlin";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "Swift Arrays";
        todo.description = "Cover the concepts of Arrays in Swift and how they differ from the Java and Kotlin ones.";
        todo.category = "Swift";

        todoArrayList.add(todo);

        return todoArrayList;
    }


    private List<SqueakProfile> buildDummySqueakProfiles() {
        List<SqueakProfile> squeakProfileArrayList = new ArrayList<>();
        SqueakProfile squeakProfile = new SqueakProfile();
        squeakProfile.name = "profile1";

        squeakProfileArrayList.add(squeakProfile);

        squeakProfile = new SqueakProfile();
        squeakProfile.name = "profile2";

        squeakProfileArrayList.add(squeakProfile);

        squeakProfile = new SqueakProfile();
        squeakProfile.name = "profile3";

        squeakProfileArrayList.add(squeakProfile);

        return squeakProfileArrayList;
    }*/

    /**
     * Migrate from:
     * version 1
     * to
     * version 2 - where the {@link SqueakProfile} has a unique constraint on
     * the address field.
     */
    @VisibleForTesting
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add the address index to the squeak profile table
            database.execSQL(
                    "CREATE UNIQUE INDEX index_profile_address ON profile (address)");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE profile ADD COLUMN uploadEnabled INTEGER NOT NULL DEFAULT 1");
            database.execSQL(
                    "ALTER TABLE profile ADD COLUMN downloadEnabled INTEGER NOT NULL DEFAULT 1");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE server (" +
                            "server_id INTEGER PRIMARY KEY NOT NULL," +
                            "serverName TEXT," +
                            "serverAddress TEXT NOT NULL)");
            database.execSQL(
                    "CREATE UNIQUE INDEX index_server_serverAddress ON server (serverAddress)");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE squeak ADD COLUMN block TEXT");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE offer (" +
                            "offerId INTEGER PRIMARY KEY NOT NULL," +
                            "squeakHash TEXT NOT NULL," +
                            "nonce TEXT NOT NULL," +
                            "preimageHash TEXT NOT NULL," +
                            "amount INT NOT NULL," +
                            "paymentRequest TEXT NOT NULL," +
                            "pubkey TEXT NOT NULL," +
                            "host TEXT NOT NULL," +
                            "port INT NOT NULL," +
                            "squeakServerId INT NOT NULL," +
                            "preimage TEXT)");
            database.execSQL(
                    "CREATE UNIQUE INDEX index_offer_squeakHash ON offer (squeakHash)");
            database.execSQL(
                    "CREATE UNIQUE INDEX index_offer_squeakServerId ON offer (squeakServerId)");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Drop the existing offer table
            database.execSQL(
                    "DROP INDEX index_offer_squeakServerId");
            database.execSQL(
                    "DROP INDEX index_offer_squeakHash");
            database.execSQL(
                    "DROP TABLE offer");

            // Create the table again
            database.execSQL(
                    "CREATE TABLE offer (" +
                            "offerId INTEGER PRIMARY KEY NOT NULL," +
                            "squeakHash TEXT NOT NULL," +
                            "nonce BLOB NOT NULL," +
                            "preimageHash TEXT NOT NULL," +
                            "amount INTEGER NOT NULL," +
                            "paymentRequest TEXT NOT NULL," +
                            "pubkey TEXT NOT NULL," +
                            "host TEXT NOT NULL," +
                            "port INTEGER NOT NULL," +
                            "squeakServerId INTEGER NOT NULL," +
                            "preimage BLOB)");
            database.execSQL(
                    "CREATE UNIQUE INDEX index_offer_squeakHash ON offer (squeakHash)");
            database.execSQL(
                    "CREATE UNIQUE INDEX index_offer_squeakServerId ON offer (squeakServerId)");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Drop the existing offer indices
            database.execSQL(
                    "DROP INDEX index_offer_squeakServerId");
            database.execSQL(
                    "DROP INDEX index_offer_squeakHash");

            // Recreate the offer indices without unique constraint
            database.execSQL(
                    "CREATE INDEX index_offer_squeakHash ON offer (squeakHash)");
            database.execSQL(
                    "CREATE INDEX index_offer_squeakServerId ON offer (squeakServerId)");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Drop the existing offer table
            database.execSQL(
                    "DROP TABLE offer");
            // Drop the existing offer indices
            database.execSQL(
                    "DROP INDEX IF EXISTS index_offer_squeakServerId");
            database.execSQL(
                    "DROP INDEX IF EXISTS index_offer_squeakHash");
            database.execSQL(
                    "DROP INDEX IF EXISTS index_offer_squeakHash_squeakServerId");

            // Create the table again
            database.execSQL(
                    "CREATE TABLE offer (" +
                            "offerId INTEGER PRIMARY KEY NOT NULL," +
                            "squeakHash TEXT NOT NULL," +
                            "nonce BLOB NOT NULL," +
                            "preimageHash TEXT NOT NULL," +
                            "amount INTEGER NOT NULL," +
                            "paymentRequest TEXT NOT NULL," +
                            "pubkey TEXT NOT NULL," +
                            "host TEXT NOT NULL," +
                            "port INTEGER NOT NULL," +
                            "squeakServerId INTEGER NOT NULL," +
                            "preimage BLOB)");
            // Create the new offer index
            database.execSQL(
                    "CREATE UNIQUE INDEX index_offer_squeakHash_squeakServerId ON offer (squeakHash, squeakServerId)");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE offer ADD COLUMN hasValidPreimage INTEGER NOT NULL DEFAULT 0");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Drop the existing offer table
            database.execSQL(
                    "DROP TABLE offer");
            // Drop the existing offer indices
            database.execSQL(
                    "DROP INDEX IF EXISTS index_offer_squeakHash_squeakServerAddress");

            // Create the table again
            database.execSQL(
                    "CREATE TABLE offer (" +
                            "offerId INTEGER PRIMARY KEY NOT NULL," +
                            "squeakHash TEXT NOT NULL," +
                            "nonce BLOB NOT NULL," +
                            "preimageHash TEXT NOT NULL," +
                            "amount INTEGER NOT NULL," +
                            "paymentRequest TEXT NOT NULL," +
                            "pubkey TEXT NOT NULL," +
                            "host TEXT NOT NULL," +
                            "port INTEGER NOT NULL," +
                            "squeakServerAddress TEXT NOT NULL," +
                            "hasValidPreimage INTEGER NOT NULL," +
                            "preimage BLOB)");
            // Create the new offer index
            database.execSQL(
                    "CREATE UNIQUE INDEX index_offer_squeakHash_squeakServerAddress ON offer (squeakHash, squeakServerAddress)");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Drop the existing squeak table
            database.execSQL(
                    "DROP TABLE squeak");

            // Drop the existing offer table
            database.execSQL(
                    "DROP TABLE offer");
            // Drop the existing offer indices
            database.execSQL(
                    "DROP INDEX IF EXISTS index_offer_squeakHash_squeakServerAddress");


            // Create the squeak table again
            database.execSQL(
                    "CREATE TABLE squeak (" +
                            "hash TEXT PRIMARY KEY NOT NULL," +
                            "hashEncContent TEXT NOT NULL," +
                            "hashReplySqk TEXT NOT NULL," +
                            "hashBlock TEXT NOT NULL," +
                            "blockHeight INTEGER NOT NULL," +
                            "scriptPubKeyBytes BLOB NOT NULL," +
                            "encryptionKey BLOB NOT NULL," +
                            "encDataKey BLOB NOT NULL," +
                            "iv BLOB NOT NULL," +
                            "time INTEGER NOT NULL," +
                            "nonce INTEGER NOT NULL," +
                            "encContent BLOB NOT NULL," +
                            "scriptSigBytes BLOB NOT NULL," +
                            "decryptionKey BLOB NOT NULL," +
                            "decryptedContentStr TEXT NOT NULL," +
                            "authorAddress TEXT NOT NULL," +
                            "block TEXT)");

            // Create the table again
            database.execSQL(
                    "CREATE TABLE offer (" +
                            "offerId INTEGER PRIMARY KEY NOT NULL," +
                            "squeakHash TEXT NOT NULL," +
                            "keyCipher BLOB NOT NULL," +
                            "iv BLOB NOT NULL," +
                            "preimageHash TEXT NOT NULL," +
                            "amount INTEGER NOT NULL," +
                            "paymentRequest TEXT NOT NULL," +
                            "pubkey TEXT NOT NULL," +
                            "host TEXT NOT NULL," +
                            "port INTEGER NOT NULL," +
                            "squeakServerAddress TEXT NOT NULL," +
                            "hasValidPreimage INTEGER NOT NULL," +
                            "preimage BLOB)");
            // Create the new offer index
            database.execSQL(
                    "CREATE UNIQUE INDEX index_offer_squeakHash_squeakServerAddress ON offer (squeakHash, squeakServerAddress)");

        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Drop the existing squeak table
            database.execSQL(
                    "DROP TABLE squeak");

            // Create the squeak table again
            database.execSQL(
                    "CREATE TABLE squeak (" +
                            "hash TEXT PRIMARY KEY NOT NULL," +
                            "hashEncContent TEXT NOT NULL," +
                            "hashReplySqk TEXT NOT NULL," +
                            "hashBlock TEXT NOT NULL," +
                            "blockHeight INTEGER NOT NULL," +
                            "scriptPubKeyBytes BLOB NOT NULL," +
                            "encryptionKey BLOB NOT NULL," +
                            "encDataKey BLOB NOT NULL," +
                            "iv BLOB NOT NULL," +
                            "time INTEGER NOT NULL," +
                            "nonce INTEGER NOT NULL," +
                            "encContent BLOB NOT NULL," +
                            "scriptSigBytes BLOB NOT NULL," +
                            "decryptionKey BLOB," +
                            "decryptedContentStr TEXT NOT NULL," +
                            "authorAddress TEXT NOT NULL," +
                            "block TEXT)");
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Drop the existing squeak table
            database.execSQL(
                    "DROP TABLE squeak");

            // Create the squeak table again
            database.execSQL(
                    "CREATE TABLE squeak (" +
                            "hash TEXT PRIMARY KEY NOT NULL," +
                            "hashEncContent TEXT NOT NULL," +
                            "hashReplySqk TEXT NOT NULL," +
                            "hashBlock TEXT NOT NULL," +
                            "blockHeight INTEGER NOT NULL," +
                            "scriptPubKeyBytes BLOB NOT NULL," +
                            "encryptionKey BLOB NOT NULL," +
                            "encDataKey BLOB NOT NULL," +
                            "iv BLOB NOT NULL," +
                            "time INTEGER NOT NULL," +
                            "nonce INTEGER NOT NULL," +
                            "encContent BLOB NOT NULL," +
                            "scriptSigBytes BLOB NOT NULL," +
                            "decryptionKey BLOB," +
                            "decryptedContentStr TEXT," +
                            "authorAddress TEXT NOT NULL," +
                            "block TEXT)");
        }
    };

}
