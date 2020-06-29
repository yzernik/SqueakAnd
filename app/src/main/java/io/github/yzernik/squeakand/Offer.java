package io.github.yzernik.squeakand;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.networkparameters.NetworkParameters;
import io.github.yzernik.squeaklib.core.Signing;

@Entity(tableName = SqueakRoomDatabase.TABLE_NAME_OFFER,
        indices = {@Index(value = {"squeakHash", "squeakServerId"}, unique = true)})
@TypeConverters({Converters.class})
public class Offer {

    public Offer() {
    }

    @Ignore
    public Offer(
            Sha256Hash squeakHash,
            byte[] nonce,
            Sha256Hash preimageHash,
            long amount,
            String paymentRequest,
            String pubkey,
            String host,
            int port,
            int squeakServerId,
            byte[] preimage) {
        this.squeakHash = squeakHash;
        this.nonce = nonce;
        this.preimageHash = preimageHash;
        this.amount = amount;
        this.paymentRequest = paymentRequest;
        this.pubkey = pubkey;
        this.host = host;
        this.port = port;
        this.squeakServerId = squeakServerId;
        this.preimage = preimage;
    }

    @Ignore
    public Offer(
            Sha256Hash squeakHash,
            byte[] nonce,
            Sha256Hash preimageHash,
            long amount,
            String paymentRequest,
            String pubkey,
            String host,
            int port,
            int squeakServerId) {
        this(squeakHash, nonce, preimageHash, amount, paymentRequest, pubkey, host, port, squeakServerId, null);
    }

    @Ignore
    public void setSqueakServerId(int squeakServerId) {
        this.squeakServerId = squeakServerId;
    }

    @PrimaryKey(autoGenerate = true)
    public int offerId;

    @NonNull
    public Sha256Hash squeakHash;

    @NonNull
    public byte[] nonce;

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
    public int squeakServerId;

    @NonNull
    public boolean hasValidPreimage;

    public byte[] preimage;

    @Override
    public String toString() {
        return "Offer("
                + "squeakHash: " + squeakHash + ", "
                + "nonce: " + nonce + ", "
                + "preimageHash: " + preimageHash + ", "
                + "amount: " + amount + ", "
                + "paymentRequest: " + paymentRequest + ", "
                + "pubkey: " + pubkey + ", "
                + "host: " + host + ", "
                + "port: " + port + ", "
                + "squeakServerId: " + squeakServerId + ", "
                + "hasValidPreimage: " + hasValidPreimage + ", "
                + "preimage: " + preimage
                + ")";
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
        return String.format("%s@%s", pubkey, host);
    }

    @Ignore
    public void setPreimage(byte[] preimage) {
        if (!Sha256Hash.of(preimage).equals(preimageHash)) {
            throw new IllegalArgumentException("Invalid preimage");
        }
        this.preimage = preimage;
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
