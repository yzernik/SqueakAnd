package io.github.yzernik.squeakand.crypto;

public class CryptoUtil {

    /**
     * XOR two bytes arrays.
     * @param a
     * @param b
     * @return
     */
    public static byte[] xor(byte[] a, byte[] b) {
        assert (a.length == b.length);

        byte[] result = new byte[a.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (((int) a[i]) ^ ((int) b[i]));
        }

        return result;
    }

}
