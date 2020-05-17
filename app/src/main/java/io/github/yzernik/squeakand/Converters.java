package io.github.yzernik.squeakand;

import androidx.room.TypeConverter;

import org.bitcoinj.core.ECKey;

import static org.bitcoinj.core.Utils.HEX;

public class Converters {

    @TypeConverter
    public static ECKey fromString(String s) {
        byte[] privKeyBytes = HEX.decode(s);
        return ECKey.fromPrivate(privKeyBytes);
    }

    @TypeConverter
    public static String keyToString(ECKey ecKey) {
        byte[] privKeyBytes = ecKey.getPrivKeyBytes();
        return HEX.encode(privKeyBytes);
    }

}
