package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
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

    // private MutableLiveData<List<ElectrumServerAddress>> liveServers;
    private ConcurrentHashMap<ElectrumServerAddress, Long> serversMap;
    private BlockingQueue<ElectrumServerAddress> peerCandidates = new LinkedBlockingQueue();

    private final ExecutorService executorService;
    private Future<String> future = null;

    public PeerDownloader(LiveElectrumPeersMap peersMap) {
        // this.liveServers = liveServers;
        this.serversMap = peersMap;
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

    public void considerAddress(ElectrumServerAddress address) {
        peerCandidates.add(address);
    }


    private void remove(ElectrumServerAddress address) {
        serversMap.remove(address);
    }

    private void add(ElectrumServerAddress address) {
        serversMap.put(address, getCurrentTimeMs());
    }

    private long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }


    /*
    public void updateLiveData() {
        Log.i(getClass().getName(), "Number of electrum peers: " + serversMap.size());
        ArrayList<ElectrumServerAddress> keyList = new ArrayList<ElectrumServerAddress>(serversMap.keySet());
        liveServers.postValue(keyList);
    }*/


    class PeerUpdateTask implements Callable<String> {

        PeerUpdateTask() {
        }

        @Override
        public String call() throws InterruptedException {
            while (true) {
                updatePeers();
                considerCandidatePeers();
                Thread.sleep(UPDATE_INTERVAL_MS);
            }
        }

        private void updatePeers() throws InterruptedException {
            for (ElectrumServerAddress address: serversMap.keySet()) {
                updatePeer(address);
            }

            // Handle the seed peers
            List<Peer> seedPeers = SeedPeers.getSeedPeers();
            for (Peer peer: seedPeers) {
                ElectrumServerAddress newAddress = addressFromPeer(peer);
                makeCandidateAddress(newAddress);
            }
        }

        private void considerCandidatePeers() throws InterruptedException {
            while (!peerCandidates.isEmpty()) {
                ElectrumServerAddress candidate = peerCandidates.take();
                handleNewAddress(candidate);
            }
        }

        private void updatePeer(ElectrumServerAddress address) throws InterruptedException {
            List<Peer> peers = getPeers(address);
            if (peers != null) {
                for (Peer peer: peers) {
                    ElectrumServerAddress newAddress = addressFromPeer(peer);
                    makeCandidateAddress(newAddress);
                }
            }

            removeIfOld(address);
        }

        private void removeIfOld(ElectrumServerAddress address) {
            Long lastConnectTimeMs = serversMap.get(address);
            if (lastConnectTimeMs != null) {
                long currentTimeMs = getCurrentTimeMs();
                if (currentTimeMs - lastConnectTimeMs > DISCONNECT_TIMEOUT_MS) {
                    remove(address);
                }
            }

        }

        private void handleNewAddress(ElectrumServerAddress address) throws InterruptedException {
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

        private void makeCandidateAddress(ElectrumServerAddress address) {
            if (address == null) {
                return;
            }

            peerCandidates.add(address);
        }

        private boolean ping(ElectrumServerAddress address) throws InterruptedException {
            List<Peer> peers = getPeers(address);
            if (peers != null) {
                return true;
            } else {
                return false;
            }
        }

        private List<Peer> getPeers(ElectrumServerAddress address) throws InterruptedException {
            InetSocketAddress socketAddress = new InetSocketAddress(address.getHost(), address.getPort());
            ElectrumClient client = new ElectrumClient(socketAddress, executorService);
            Future<SubscribePeersResponse> responseFuture = client.subscribePeers();
            try {
                SubscribePeersResponse response = responseFuture.get(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                return response.peers;
            } catch (ExecutionException | TimeoutException e) {
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw e;
            }
        }

        private ElectrumServerAddress addressFromPeer(Peer peer) {
            InetSocketAddress address;
            Integer port = getPort(peer);
            if (port == null) {
                return null;
            }

            // Try creating a new address from the peer hostname.
            return new ElectrumServerAddress(peer.hostname, port);
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
