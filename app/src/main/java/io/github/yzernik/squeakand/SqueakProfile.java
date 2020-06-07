package io.github.yzernik.squeakand;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;

import io.github.yzernik.squeakand.networkparameters.NetworkParameters;
import io.github.yzernik.squeaklib.core.Signing;


@Entity(
        tableName = SqueakRoomDatabase.TABLE_NAME_PROFILE,
        indices = {@Index(value = {"address"}, unique = true)})
@TypeConverters({Converters.class})
public class SqueakProfile implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int profile_id;

    public String name;

    public Signing.BitcoinjKeyPair keyPair;

    public String address;

    public boolean uploadEnabled;

    public boolean downloadEnabled;

    public SqueakProfile() {
    }

    @Ignore
    public SqueakProfile(String name, Signing.BitcoinjKeyPair keyPair) {
        this(name, keyPair.getPublicKey().getAddress(NetworkParameters.getNetworkParameters()));
        this.keyPair = keyPair;
    }

    @Ignore
    public SqueakProfile(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Ignore
    public String getName() {
        return name;
    }

    @Ignore
    public Signing.BitcoinjKeyPair getKeyPair() {
        return keyPair;
    }

    @Ignore
    public String getAddress() {
        return address;
    }

    @Ignore
    public int getProfileId() {
        return profile_id;
    }

    @Ignore
    public boolean isSigningProfile() {
        return keyPair != null;
    }

    @Override
    public String toString() {
        return "SqueakProfile("
                + "name: " + name + ", "
                + "profile_id: " + profile_id + ", "
                + "address: " + getAddress()
                + ")";
    }

}