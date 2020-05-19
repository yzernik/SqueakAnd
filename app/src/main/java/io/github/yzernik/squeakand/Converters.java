package io.github.yzernik.squeakand;

import androidx.room.TypeConverter;

import org.bitcoinj.core.ECKey;

import java.security.KeyPair;

import io.github.yzernik.squeaklib.core.Signing;

import static org.bitcoinj.core.Utils.HEX;

public class Converters {

    @TypeConverter
    public static Signing.BitcoinjKeyPair fromString(String s) {
        byte[] privKeyBytes = HEX.decode(s);
        ECKey ecKey = ECKey.fromPrivate(privKeyBytes);
        return new Signing.BitcoinjKeyPair(ecKey);
    }

    @TypeConverter
    public static String keyToString(Signing.BitcoinjKeyPair keyPair) {
        ECKey ecKey = keyPair.getEcKey();
        byte[] privKeyBytes = ecKey.getPrivKeyBytes();
        return HEX.encode(privKeyBytes);
    }

}
