package io.github.yzernik.squeakand;

import org.junit.Test;

import java.util.List;

import io.github.yzernik.electrumclient.subscribepeers.Peer;
import io.github.yzernik.squeakand.blockchain.SeedPeers;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SeedPeersUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getSeedPeers() {
        List<Peer> peers = SeedPeers.getSeedPeers();

        assert (peers.size() > 0);
        assertEquals("electrum.nute.net", peers.get(0).hostname);
    }


}