package io.github.yzernik.squeakand;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

import io.github.yzernik.squeakand.blockchain.Blockchain;
import io.github.yzernik.squeakand.blockchain.DummyBlockchain;
import io.github.yzernik.squeakand.blockchain.ElectrumClient;
import io.github.yzernik.squeaklib.core.Signing;
import io.github.yzernik.squeaklib.core.Squeak;

import static junit.framework.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class ElectrumClientTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // Nothing
    }

    @After
    public void teardown() {
        // Nothing
    }

    @Test
    public void subscribeServer() throws Exception {
        ElectrumClient electrumClient = new ElectrumClient("currentlane.lovebitco.in", 50001);
        electrumClient.start();

        Stream<String> blockStringStream = electrumClient.getBlocks();
        String currentBlockString = blockStringStream.findFirst().get();

        // assertEquals("my block", currentBlockString);
        assert currentBlockString.startsWith("{\"jsonrpc\": \"2.0\", \"result\":");
    }

}