package io.github.yzernik.squeakand.lnd;

import lnrpc.Rpc;

import static org.bitcoinj.core.Utils.HEX;

public class ChannelPointUtil {

    public static String stringFromChannelPoint(Rpc.ChannelPoint channelPoint) {
        // Get the funding tx id.
        String fundingTxStr = channelPoint.getFundingTxidStr();
        if (fundingTxStr == null || fundingTxStr.isEmpty()) {
            byte[] fundingTxBytes = channelPoint.getFundingTxidBytes().toByteArray();
            fundingTxStr = HEX.encode(reverse(fundingTxBytes));
        }

        // Get the output index.
        int outputIndex = channelPoint.getOutputIndex();

        return String.format("%s:%s", fundingTxStr, outputIndex);
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

    /* function that reverses array and stores it
   in another array*/
    static byte[] reverse(byte a[]) {
        int n = a.length;
        byte[] b = new byte[n];
        int j = n;
        for (int i = 0; i < n; i++) {
            b[j - 1] = a[i];
            j = j - 1;
        }
        return b;
    }

}
