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

    public Sha256Hash hashEncContent;
    public Sha256Hash hashReplySqk;
    public Sha256Hash hashBlock;
    public long blockHeight;
    public byte[] scriptPubKeyBytes;
    public Sha256Hash hashDataKey;
    public byte[] iv;
    public long time;
    public long nonce;
    public byte[] encContent;
    public byte[] scriptSigBytes;
    public byte[] dataKey;
    public String decryptedContentStr;
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
        this.hashDataKey = squeak.getHashDataKey();
        this.iv = squeak.getVchIv();
        this.time = squeak.getTime();
        this.nonce = squeak.getNonce();
        this.encContent = squeak.getEncContent();
        this.scriptSigBytes = squeak.getScriptSigBytes();
        this.dataKey = squeak.getDataKey();
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
                hashDataKey,
                iv,
                time,
                nonce,
                encContent,
                scriptSigBytes,
                dataKey
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

}