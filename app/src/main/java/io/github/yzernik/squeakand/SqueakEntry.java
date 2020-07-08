package io.github.yzernik.squeakand;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

import java.io.Serializable;

import io.github.yzernik.squeakand.networkparameters.NetworkParameters;
import io.github.yzernik.squeaklib.core.Squeak;


@Entity(tableName = SqueakRoomDatabase.TABLE_NAME_SQUEAK)
@TypeConverters({Converters.class})
public class SqueakEntry implements Serializable {

    @PrimaryKey
    @NonNull
    public Sha256Hash hash;

    @NonNull
    public Sha256Hash hashEncContent;

    @NonNull
    public Sha256Hash hashReplySqk;

    @NonNull
    public Sha256Hash hashBlock;

    @NonNull
    public long blockHeight;

    @NonNull
    public byte[] scriptPubKeyBytes;

    @NonNull
    public byte[] encryptionKey;

    @NonNull
    public byte[] encDataKey;

    @NonNull
    public byte[] iv;

    @NonNull
    public long time;

    @NonNull
    public long nonce;

    @NonNull
    public byte[] encContent;

    @NonNull
    public byte[] scriptSigBytes;

    public byte[] decryptionKey;

    public String decryptedContentStr;

    @NonNull
    public String authorAddress;

    public Block block;

    public SqueakEntry() {
    }

    @Ignore
    public SqueakEntry(Squeak squeak) {
        this(squeak, null);
    }

    @Ignore
    public SqueakEntry(Squeak squeak, Block block) {
        this.hash = squeak.getHash();
        this.hashEncContent = squeak.getHashEncContent();
        this.hashReplySqk = squeak.getHashReplySqk();
        this.hashBlock = squeak.getHashBlock();
        this.blockHeight = squeak.getBlockHeight();
        this.scriptPubKeyBytes = squeak.getScriptPubKeyBytes();
        this.encryptionKey = squeak.getEncryptionKey().getBytes();
        this.encDataKey = squeak.getEncDataKeyBytes();
        this.iv = squeak.getVchIv();
        this.time = squeak.getTime();
        this.nonce = squeak.getNonce();
        this.encContent = squeak.getEncContent();
        this.scriptSigBytes = squeak.getScriptSigBytes();
        this.decryptionKey = squeak.hasDecryptionKey() ? squeak.getDecryptionKey().getBytes() : null;
        this.decryptedContentStr = getDecryptedContentStr(squeak);
        this.authorAddress = squeak.getAddress().toString();
        this.block = block;
    }

    @Ignore
    public Squeak getSqueak() {
        return new Squeak(
                NetworkParameters.getNetworkParameters(),
                hashEncContent,
                hashReplySqk,
                hashBlock,
                blockHeight,
                scriptPubKeyBytes,
                encryptionKey,
                encDataKey,
                iv,
                time,
                nonce,
                encContent,
                scriptSigBytes,
                decryptionKey
        );
    }

    @Ignore
    public Block getBlock() {
        return this.block;
    }

    @Ignore
    public boolean isReply() {
        return !hashReplySqk.equals(Sha256Hash.ZERO_HASH);
    }

    @Ignore
    private String getDecryptedContentStr(Squeak squeak) {
        try {
            return squeak.getDecryptedContentStr();
        } catch (Exception e) {
            return null;
        }
    }

    @Ignore
    public String getDecryptedContentStr() {
        return decryptedContentStr;
    }

    @Ignore
    public boolean hasDecryptionKey() {
        return decryptionKey != null;
    }

}