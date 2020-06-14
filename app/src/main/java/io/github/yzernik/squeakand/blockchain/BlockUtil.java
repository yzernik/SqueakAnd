package io.github.yzernik.squeakand.blockchain;

import org.bitcoinj.core.BitcoinSerializer;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;

import io.github.yzernik.electrumclient.GetHeaderResponse;
import io.github.yzernik.electrumclient.SubscribeHeadersResponse;

import static org.bitcoinj.core.Utils.HEX;

public class BlockUtil {

    public static BlockInfo parseHeaderResponse(SubscribeHeadersResponse response) {
        Block block = parseBlockHeader(response.hex);
        return new BlockInfo(block, response.height);
    }

    public static Block parseGetHeaderResponse(GetHeaderResponse response) {
        return parseBlockHeader(response.hex);
    }

    public static Block parseBlockHeader(String hex) {
        NetworkParameters networkParameters = io.github.yzernik.squeakand.networkparameters.NetworkParameters.getNetworkParameters();
        BitcoinSerializer bitcoinSerializer = new BitcoinSerializer(networkParameters, false);
        byte[] blockBytes = HEX.decode(hex);
        return bitcoinSerializer.makeBlock(blockBytes);
    }

}
