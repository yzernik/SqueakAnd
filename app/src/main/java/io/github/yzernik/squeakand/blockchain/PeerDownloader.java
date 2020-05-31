package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.yzernik.electrumclient.ElectrumClient;
import io.github.yzernik.electrumclient.subscribepeers.Peer;
import io.github.yzernik.electrumclient.subscribepeers.SubscribePeersResponse;

public class PeerDownloader {

    private static final int UPDATE_INTERVAL_MS = 60000; // 1 minute
    private static final int DISCONNECT_TIMEOUT_MS = 3600000; // 1 hour
    private static final int CONNECT_TIMEOUT_MS = 10000; // 10 seconds
    private static final int MAX_SERVERS = 100;

    private MutableLiveData<List<InetSocketAddress>> liveServers;
    private ConcurrentHashMap<InetSocketAddress, Long> serversMap;

    private final ExecutorService executorService;
    private Future<String> future = null;


    public PeerDownloader(MutableLiveData<List<InetSocketAddress>> liveServers, ConcurrentHashMap<InetSocketAddress, Long> serversMap) {
        this.liveServers = liveServers;
        this.serversMap = serversMap;
        this.executorService =  Executors.newFixedThreadPool(10);
    }

    public synchronized void keepPeersUpdated() {
        if (future != null) {
            future.cancel(true);
        }

        PeerUpdateTask peerUpdateTask = new PeerUpdateTask();
        Log.i(getClass().getName(), "Submitting new peer update task.");
        future = executorService.submit(peerUpdateTask);
    }

    private void remove(InetSocketAddress address) {
        serversMap.remove(address);
        updateLiveData();
    }

    private void add(InetSocketAddress address) {
        serversMap.put(address, getCurrentTimeMs());
        updateLiveData();
    }

    private long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }

    public void updateLiveData() {
        System.out.println("serversMap size: " + serversMap.size());
        ArrayList<InetSocketAddress> keyList = new ArrayList<InetSocketAddress>(serversMap.keySet());
        liveServers.postValue(keyList);
    }


    class PeerUpdateTask implements Callable<String> {

        PeerUpdateTask() {
        }

        @Override
        public String call() throws InterruptedException {
            while (true) {
                updatePeers();
                Thread.sleep(UPDATE_INTERVAL_MS);
            }
        }

        private void updatePeers() throws InterruptedException {
            for (InetSocketAddress address: serversMap.keySet()) {
                updatePeer(address);
            }

            // Handle the seed peers
            List<Peer> seedPeers = SeedPeers.getSeedPeers();
            for (Peer peer: seedPeers) {
                InetSocketAddress newAddress = addressFromPeer(peer);
                handleNewAddress(newAddress);
            }
        }

        private void updatePeer(InetSocketAddress address) throws InterruptedException {
            List<Peer> peers = getPeers(address);
            if (peers != null) {
                for (Peer peer: peers) {
                    InetSocketAddress newAddress = addressFromPeer(peer);
                    handleNewAddress(newAddress);
                }
            }

            removeIfOld(address);
        }

        private void removeIfOld(InetSocketAddress address) {
            Long lastConnectTimeMs = serversMap.get(address);
            if (lastConnectTimeMs != null) {
                long currentTimeMs = getCurrentTimeMs();
                if (currentTimeMs - lastConnectTimeMs > DISCONNECT_TIMEOUT_MS) {
                    remove(address);
                }
            }

        }

        private void handleNewAddress(InetSocketAddress address) throws InterruptedException {
            Log.i(getClass().getName(), "Handling address: " + address);
            if (address == null) {
                return;
            }

            if (serversMap.size() >= MAX_SERVERS) {
                return;
            }

            if (serversMap.containsKey(address)) {
                return;
            }

            boolean canConnect = ping(address);
            if (canConnect) {
                add(address);
            }
        }

        private boolean ping(InetSocketAddress address) throws InterruptedException {
            List<Peer> peers = getPeers(address);
            if (peers != null) {
                return true;
            } else {
                return false;
            }
        }

        private List<Peer> getPeers(InetSocketAddress address) throws InterruptedException {
            ElectrumClient client = new ElectrumClient(address, executorService);
            Future<SubscribePeersResponse> responseFuture = client.subscribePeers();
            try {
                SubscribePeersResponse response = responseFuture.get(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                return response.peers;
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw e;
            }
        }

        private InetSocketAddress addressFromPeer(Peer peer) {
            InetSocketAddress address;
            Integer port = getPort(peer);
            if (port == null) {
                return null;
            }

            // Try creating a new address from the peer hostname.
            address = new InetSocketAddress(peer.hostname, port);
            return address;
        }

        private Integer getPort(Peer peer) {
            for(String feature: peer.features.features) {
                if (feature.startsWith("t")) {
                    String portString = feature.substring(1);
                    return Integer.valueOf(portString);
                }
            }
            return null;
        }

    }

}
