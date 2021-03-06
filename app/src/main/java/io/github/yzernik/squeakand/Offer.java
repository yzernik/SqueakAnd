package io.github.yzernik.squeakand;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.server.SqueakServerAddress;

@Entity(tableName = SqueakRoomDatabase.TABLE_NAME_OFFER,
        indices = {@Index(value = {"squeakHash", "squeakServerAddress"}, unique = true)})
@TypeConverters({Converters.class})
public class Offer {

    @PrimaryKey(autoGenerate = true)
    public int offerId;

    @NonNull
    public Sha256Hash squeakHash;

    @NonNull
    public byte[] keyCipher;

    @NonNull
    public byte[] iv;

    @NonNull
    public Sha256Hash preimageHash;

    @NonNull
    public long amount;

    @NonNull
    public String paymentRequest;

    @NonNull
    public String pubkey;

    @NonNull
    public String host;

    @NonNull
    public int port;

    @NonNull
    public SqueakServerAddress squeakServerAddress;

    @NonNull
    public boolean hasValidPreimage;

    public byte[] preimage;

    @Override
    public String toString() {
        return "Offer("
                + "squeakHash: " + squeakHash + ", "
                + "keyCipher: " + keyCipher + ", "
                + "iv: " + iv + ", "
                + "preimageHash: " + preimageHash + ", "
                + "amount: " + amount + ", "
                + "paymentRequest: " + paymentRequest + ", "
                + "pubkey: " + pubkey + ", "
                + "host: " + host + ", "
                + "port: " + port + ", "
                + "squeakServerAddress: " + squeakServerAddress + ", "
                + "hasValidPreimage: " + hasValidPreimage + ", "
                + "preimage: " + preimage
                + ")";
    }

    public Offer() {
    }

    @Ignore
    public Offer(
            Sha256Hash squeakHash,
            byte[] keyCipher,
            byte[] iv,
            Sha256Hash preimageHash,
            long amount,
            String paymentRequest,
            String pubkey,
            String host,
            int port,
            SqueakServerAddress squeakServerAddress,
            byte[] preimage) {
        this.squeakHash = squeakHash;
        this.keyCipher = keyCipher;
        this.iv = iv;
        this.preimageHash = preimageHash;
        this.amount = amount;
        this.paymentRequest = paymentRequest;
        this.pubkey = pubkey;
        this.host = host;
        this.port = port;
        this.squeakServerAddress = squeakServerAddress;
        this.preimage = preimage;
    }

    @Ignore
    public Offer(
            Sha256Hash squeakHash,
            byte[] keyCipher,
            byte[] iv,
            Sha256Hash preimageHash,
            long amount,
            String paymentRequest,
            String pubkey,
            String host,
            int port,
            SqueakServerAddress squeakServerAddress) {
        this(squeakHash, keyCipher, iv, preimageHash, amount, paymentRequest, pubkey, host, port, squeakServerAddress, null);
    }

    @Ignore
    public void setSqueakServerAddress(SqueakServerAddress squeakServerAddress) {
        this.squeakServerAddress = squeakServerAddress;
    }

    @Ignore
    public int getOfferId() {
        return offerId;
    }

    @Ignore
    public long getAmount() {
        return amount;
    }

    @Ignore
    public Sha256Hash getSqueakHash() {
        return squeakHash;
    }

    @Ignore
    public String getLightningAddress() {
        return String.format("%s@%s", pubkey, getLightningHost());
    }

    @Ignore
    public String getLightningHost() {
        return String.format("%s:%d", host, port);
    }

    @Ignore
    public String getPubkey() {
        return pubkey;
    }

    @Ignore
    public SqueakServerAddress getSqueakServerAddress() {
        return squeakServerAddress;
    }

    @Ignore
    public void setPreimage(byte[] preimage) {
        if (!Sha256Hash.of(preimage).equals(preimageHash)) {
            throw new IllegalArgumentException("Invalid preimage");
        }
        this.preimage = preimage;
    }

    @Ignore
    public boolean hasPreimage() {
        return preimage != null;
    }

    @Ignore
    public void setHasValidPreimage(boolean hasValidPreimage) {
        this.hasValidPreimage = hasValidPreimage;
    }

    @Ignore
    public boolean getHasValidPreimage() {
        return hasValidPreimage;
    }

}
