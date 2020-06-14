package io.github.yzernik.squeakand;

import androidx.room.TypeConverter;

import org.bitcoinj.core.BitcoinSerializer;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.MessageSerializer;
import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.networkparameters.NetworkParameters;
import io.github.yzernik.squeakand.server.SqueakServerAddress;
import io.github.yzernik.squeaklib.core.Signing;

import static org.bitcoinj.core.Utils.HEX;

public class Converters {

    @TypeConverter
    public static Signing.BitcoinjKeyPair fromString(String s) {
        if (s == null) {
            return null;
        }

        byte[] privKeyBytes = HEX.decode(s);
        ECKey ecKey = ECKey.fromPrivate(privKeyBytes);
        return new Signing.BitcoinjKeyPair(ecKey);
    }

    @TypeConverter
    public static String keyToString(Signing.BitcoinjKeyPair keyPair) {
        if (keyPair == null) {
            return null;
        }

        ECKey ecKey = keyPair.getEcKey();
        byte[] privKeyBytes = ecKey.getPrivKeyBytes();
        return HEX.encode(privKeyBytes);
    }

    @TypeConverter
    public static byte[] bytesFromString(String s) {
        return HEX.decode(s);
    }

    @TypeConverter
    public static String bytesToString(byte[] bytes) {
        return HEX.encode(bytes);
    }

    @TypeConverter
    public static Sha256Hash hashFromString(String s) {
        byte[] hashBytes = HEX.decode(s);
        return Sha256Hash.wrap(hashBytes);
    }

    @TypeConverter
    public static String hashToString(Sha256Hash hash) {
        byte[] hashBytes = hash.getBytes();
        return HEX.encode(hashBytes);
    }

    @TypeConverter
    public static SqueakServerAddress serverAddressFromString(String s) {
        return SqueakServerAddress.fromString(s);
    }

    @TypeConverter
    public static String serverAddressToString(SqueakServerAddress squeakServerAddress) {
        return squeakServerAddress.toString();
    }

    @TypeConverter
    public static Block blockFromString(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        MessageSerializer serializer = new BitcoinSerializer(NetworkParameters.getNetworkParameters(), true);
        byte[] bytes = HEX.decode(s);
        return serializer.makeBlock(bytes);
    }

    @TypeConverter
    public static String blockToString(Block block) {
        if (block == null) {
            return null;
        }

        byte[] bytes = block.bitcoinSerialize();
        return HEX.encode(bytes);
    }

}
