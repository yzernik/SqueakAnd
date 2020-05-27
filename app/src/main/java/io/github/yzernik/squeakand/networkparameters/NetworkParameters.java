package io.github.yzernik.squeakand.networkparameters;

import org.bitcoinj.params.MainNetParams;

public class NetworkParameters {

    public static org.bitcoinj.core.NetworkParameters getNetworkParameters() {
        return MainNetParams.get();
    }

}
