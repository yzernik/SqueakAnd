package io.github.yzernik.squeakand.blockchain;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.List;

import io.github.yzernik.electrumclient.subscribepeers.Peer;
import io.github.yzernik.electrumclient.subscribepeers.SubscribePeersResponse;
import io.github.yzernik.electrumclient.subscribepeers.SubscribePeersResponseDeserializer;

public class SeedPeers {
    private static final String peersJson = "[[\"2001:41d0:a:4c49::82\", \"electrum.nute.net\", [\"v1.4.1\", \"s17315\"]], [\"165.73.105.234\", \"electrumx.ddns.net\", [\"v1.4.2\", \"s50005\"]], [\"73.0.216.24\", \"gall.pro\", [\"v1.4.2\", \"s50002\"]], [\"2604:a880:800:c1::233:1\", \"2604:a880:800:c1::233:1\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"2a01:4f8:201:8367::2\", \"2a01:4f8:201:8367::2\", [\"v1.4.2\", \"s50002\"]], [\"148.251.87.112\", \"fortress.qtornado.com\", [\"v1.4.2\", \"s443\"]], [\"213.109.162.82\", \"213.109.162.82\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"2a03:b0c0:2:f0::c4:b001\", \"2a03:b0c0:2:f0::c4:b001\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"217.182.196.73\", \"ns3079942.ip-217-182-196.eu\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"148.251.22.104\", \"148.251.22.104\", [\"v1.4.2\", \"s50002\"]], [\"88.99.29.15\", \"hodlers.beer\", [\"v1.4.2\", \"s50002\"]], [\"85.215.92.224\", \"ex01.axalgo.com\", [\"v1.4.2\", \"s50002\"]], [\"5.103.137.146\", \"btc.electroncash.dk\", [\"v1.4.2\", \"s60002\"]], [\"220.233.178.199\", \"btc.rossbennetts.com\", [\"v1.4.2\", \"s50082\", \"t50081\"]], [\"5.56.194.105\", \"electrum.fedaykin.eu\", [\"v1.4.2\", \"s50006\"]], [\"51.254.199.134\", \"blkhub.net\", [\"v1.4.2\", \"s50002\"]], [\"85.214.220.236\", \"ex02.axalgo.com\", [\"v1.4.2\", \"s50002\"]], [\"157.245.172.236\", \"157.245.172.236\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"37.211.78.253\", \"endthefed.onthewifi.com\", [\"v1.4.2\", \"s50002\"]], [\"167.172.42.31\", \"167.172.42.31\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"51.15.106.98\", \"vps4.hsmiths.com\", [\"v1.4.2\", \"s50002\"]], [\"2a03:4000:36:29a:b8f4:38ff:fe1f:becc\", \"2a03:4000:36:29a:b8f4:38ff:fe1f:becc\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"2400:6180:0:d1::86b:e001\", \"2400:6180:0:d1::86b:e001\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"46.229.238.187\", \"bitcoins.sk\", [\"v1.4.2\", \"s56002\", \"t56001\"]], [\"104.248.139.211\", \"104.248.139.211\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"217.182.196.69\", \"ns3079938.ip-217-182-196.eu\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"142.93.6.38\", \"142.93.6.38\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"178.33.228.210\", \"electrum2.privateservers.network\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"104.219.251.30\", \"satoshi.fan\", [\"v1.4.2\", \"s50002\"]], [\"2a02:c207:3003:8337::1\", \"2a02:c207:3003:8337::1\", [\"v1.4.2\", \"s50002\"]], [\"178.62.80.20\", \"178.62.80.20\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"2601:602:8d80:b63:dcc3:26ff:fe77:bd7f\", \"electrumx-core.1209k.com\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"185.163.44.46\", \"cc.mivocloud.com\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"85.10.199.107\", \"ndnd.selfhost.eu\", [\"v1.4.2\", \"s50002\"]], [\"2604:a880:400:d1::849:6001\", \"2604:a880:400:d1::849:6001\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"185.66.143.178\", \"btc.litepay.ch\", [\"v1.4.2\", \"s50002\"]], [\"104.244.222.228\", \"104.244.222.228\", [\"v1.4.2\", \"s50001\"]], [\"2a03:b0c0:3:e0::b3:f001\", \"2a03:b0c0:3:e0::b3:f001\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"70.189.171.22\", \"2ex.digitaleveryware.com\", [\"v1.4.2\", \"s50002\"]], [\"51.255.149.249\", \"xtrum.com\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"46.4.106.110\", \"46.4.106.110\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"2a01:4f8:202:3e6::2\", \"electrum.emzy.de\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"78.47.3.190\", \"electrum.dnshome.de\", [\"v1.4.2\", \"s50002\"]], [\"76.64.169.204\", \"0.electrumx.ggez.win\", [\"v1.4.2\", \"s50002\"]], [\"173.212.253.26\", \"2AZZARITA.hopto.org\", [\"v1.4.2\", \"s50006\", \"t50001\"]], [\"94.16.175.196\", \"maltokyo.asuscomm.com\", [\"v1.4.2\", \"s50002\"]], [\"13.53.116.250\", \"electrum.aantonop.com\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"203.132.95.10\", \"bitcoin.aranguren.org\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"2604:a880:2:d1::63:4001\", \"2604:a880:2:d1::63:4001\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"108.183.77.12\", \"electrumx.kenrufe.com\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"109.248.206.13\", \"109.248.206.13\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"5.189.153.179\", \"thanos.xskyx.net\", [\"v1.4.2\", \"s50002\"]], [\"2a03:b0c0:1:d0::3076:7001\", \"2a03:b0c0:1:d0::3076:7001\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"85.212.72.68\", \"electrumx.schulzemic.net\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"167.172.226.175\", \"167.172.226.175\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"96.237.231.17\", \"caleb.vegas\", [\"v1.4.2\", \"s50002\"]], [\"88.198.39.205\", \"electrum.jochen-hoenicke.de\", [\"v1.4.2\", \"s50005\", \"t50003\"]], [\"217.115.11.163\", \"ex.btcmp.com\", [\"v1.4.2\", \"s50002\"]], [\"68.183.188.105\", \"68.183.188.105\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"71.73.18.32\", \"electrumx.alexridevski.net\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"104.248.145.89\", \"bitcoin.lukechilds.co\", [\"v1.4.2\", \"s50002\", \"t50001\"]], [\"52.1.56.181\", \"52.1.56.181\", [\"v1.4.2\", \"s50002\", \"t50001\"]]]";

    public static List<Peer> getSeedPeers() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule customModule = new SimpleModule("SubscribePeersModule", new Version(0, 1, 0, null));
        customModule.addDeserializer(SubscribePeersResponse.class, new SubscribePeersResponseDeserializer());
        mapper.registerModule(customModule);

        try {
            SubscribePeersResponse response = mapper.readValue(peersJson, SubscribePeersResponse.class);
            return response.peers;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
