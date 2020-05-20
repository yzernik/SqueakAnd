package io.github.yzernik.squeakand;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.params.MainNetParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.Blockchain;
import io.github.yzernik.squeakand.blockchain.DummyBlockchain;
import io.github.yzernik.squeaklib.core.Signing;
import io.github.yzernik.squeaklib.core.Squeak;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4.class)
public class SqueakDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private SqueakDao mSqueakDao;
    private SqueakProfileDao mSqueakProfileDao;
    private SqueakRoomDatabase mDb;
    private Blockchain blockchain;
    private Signing.BitcoinjKeyPair keyPair;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        mDb = Room.inMemoryDatabaseBuilder(context, SqueakRoomDatabase.class)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build();
        mSqueakDao = mDb.squeakDao();
        mSqueakProfileDao = mDb.squeakProfileDao();
        blockchain = new DummyBlockchain();
        keyPair = new Signing.BitcoinjKeyPair();
    }

    @After
    public void closeDb() {
        mDb.close();
    }

    @Test
    public void insertAndGetSqueak() throws Exception {
        Squeak squeak = createSqeakWithText("hello");
        SqueakEntry squeakEntry = new SqueakEntry(squeak);
        mSqueakDao.insert(squeakEntry);
        List<SqueakEntry> allSqueaks = LiveDataTestUtil.getValue(mSqueakDao.getSqueaks());
        assertEquals(allSqueaks.get(0).getSqueak().getHash(), squeak.getHash());
    }

    @Test
    public void getAllSqueaks() throws Exception {
        Squeak squeak = createSqeakWithText("aaa");
        mSqueakDao.insert(new SqueakEntry(squeak));
        Squeak squeak2 = createSqeakWithText("bbb");
        mSqueakDao.insert(new SqueakEntry(squeak2));
        List<SqueakEntry> allSqueaks = LiveDataTestUtil.getValue(mSqueakDao.getSqueaks());

        Set<Sha256Hash> hashesFromSqueaks = new HashSet<>();
        Set<Sha256Hash> hashesFromDB = new HashSet<>();
        hashesFromDB.add(allSqueaks.get(0).getSqueak().getHash());
        hashesFromDB.add(allSqueaks.get(1).getSqueak().getHash());
        hashesFromSqueaks.add(squeak.getHash());
        hashesFromSqueaks.add(squeak2.getHash());

        assertEquals(hashesFromDB, hashesFromSqueaks);
    }

    @Test
    public void getAllSqueaksWithProfile() throws Exception {
        SqueakProfile squeakProfile = new SqueakProfile("profile1", keyPair);
        mSqueakProfileDao.insert(squeakProfile);

        Signing.BitcoinjKeyPair otherKeyPair = new Signing.BitcoinjKeyPair();

        Squeak squeak1 = createSqeakWithText("aaa");
        mSqueakDao.insert(new SqueakEntry(squeak1));
        Squeak squeak2 = createSqeakWithText("bbb", otherKeyPair);
        mSqueakDao.insert(new SqueakEntry(squeak2));
        List<SqueakEntryWithProfile> allSqueaks = LiveDataTestUtil.getValue(mSqueakDao.getSqueaksWithProfile());

        Set<Sha256Hash> hashesFromSqueaks = new HashSet<>();
        Set<Sha256Hash> hashesFromDB = new HashSet<>();
        hashesFromDB.add(allSqueaks.get(0).squeakEntry.getSqueak().getHash());
        hashesFromDB.add(allSqueaks.get(1).squeakEntry.getSqueak().getHash());
        hashesFromSqueaks.add(squeak1.getHash());
        hashesFromSqueaks.add(squeak2.getHash());

        // Because of LEFT JOIN, only one of the query results should have a profile attached.
        assertEquals(allSqueaks.get(0).squeakProfile, null);
        assert allSqueaks.get(1).squeakProfile.getAddress().equals(squeak1.getAddress());
        assertEquals(hashesFromDB, hashesFromSqueaks);
    }


    @Test
    public void deleteAll() throws Exception {
        Squeak squeak = createSqeakWithText("squeak1");
        mSqueakDao.insert(new SqueakEntry(squeak));
        Squeak squeak2 = createSqeakWithText("squeak2");
        mSqueakDao.insert(new SqueakEntry(squeak2));
        mSqueakDao.deleteAll();
        List<SqueakEntry> allSqueaks = LiveDataTestUtil.getValue(mSqueakDao.getSqueaks());
        assertTrue(allSqueaks.isEmpty());
    }

    private Squeak createSqeakWithText(String text, Signing.BitcoinjKeyPair keyPair) throws Exception {
        BlockInfo latestBlock = blockchain.getLatestBlock();
        return Squeak.makeSqueakFromStr(
                MainNetParams.get(),
                keyPair,
                text,
                latestBlock.getHeight(),
                latestBlock.getHash(),
                System.currentTimeMillis() / 1000,
                Sha256Hash.ZERO_HASH
        );
    }

    private Squeak createSqeakWithText(String text) throws Exception {
        return createSqeakWithText(text, keyPair);
    }
}
