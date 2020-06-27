package io.github.yzernik.squeakand.lnd;

import lnrpc.Rpc;

public class ChannelPointUtil {

    public static String stringFromChannelPoint(Rpc.ChannelPoint channelPoint) {
        return String.format("%s:%s", channelPoint.getFundingTxidStr(), channelPoint.getOutputIndex());
    }

    public static Rpc.ChannelPoint channelPointFromString(String channelPointString) {
        String[] parts = channelPointString.split(":");
        String fundingTx = parts[0];
        int outputIndex = Integer.parseInt(parts[1]);

        Rpc.ChannelPoint channelPoint = Rpc.ChannelPoint.newBuilder()
                .setFundingTxidStr(fundingTx)
                .setOutputIndex(outputIndex)
                .build();

        return channelPoint;
    }

}
