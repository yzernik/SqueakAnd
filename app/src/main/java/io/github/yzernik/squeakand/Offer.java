package io.github.yzernik.squeakand;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.bitcoinj.core.Sha256Hash;

@Entity(tableName = SqueakRoomDatabase.TABLE_NAME_OFFER,
        indices = {@Index(value = {"squeakHash"}, unique = true), @Index(value = {"squeakServerId"}, unique = true)})
@TypeConverters({Converters.class})
public class Offer {

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
    public byte[] preimage;

}
