package io.github.yzernik.squeakand;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.core.util.Pair;
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

import java.util.List;

import io.github.yzernik.squeakand.blockchain.Blockchain;
import io.github.yzernik.squeaklib.core.Signing;
import io.github.yzernik.squeaklib.core.Squeak;

import static junit.framework.Assert.assertEquals;


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
    private TodoRoomDatabase mDb;
    private Blockchain blockchain;
    private Signing.KeyPair keyPair;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        mDb = Room.inMemoryDatabaseBuilder(context, TodoRoomDatabase.class)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build();
        mSqueakDao = mDb.squeakDao();
        blockchain = new SqueakTestUtils.DummyBlockchain();
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
/*

    @Test
    public void getAllTodos() throws Exception {
        Todo todo = new Todo("aaa");
        mTodoDao.insert(todo);
        Todo todo2 = new Todo("bbb");
        mTodoDao.insert(todo2);
        List<Todo> allTodos = LiveDataTestUtil.getValue(mTodoDao.getAlphabetizedTodos());
        assertEquals(allTodos.get(0).getName(), todo.getName());
        assertEquals(allTodos.get(1).getName(), todo2.getName());
    }

    @Test
    public void deleteAll() throws Exception {
        Todo todo = new Todo("todo");
        mSqueakDao.insert(todo);
        Todo todo2 = new Todo("todo2");
        mSqueakDao.insert(todo2);
        mSqueakDao.deleteAll();
        List<Todo> allTodos = LiveDataTestUtil.getValue(mSqueakDao.getAlphabetizedTodos());
        assertTrue(allTodos.isEmpty());
    }
*/

    private Squeak createSqeakWithText(String text) throws Exception {
        Pair<Sha256Hash, Integer> latestBlock = blockchain.getLatestBlock();
        return Squeak.makeSqueakFromStr(
                MainNetParams.get(),
                keyPair,
                text,
                latestBlock.second,
                latestBlock.first,
                System.currentTimeMillis(),
                Sha256Hash.ZERO_HASH
        );
    }
}
