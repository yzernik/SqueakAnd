package io.github.yzernik.squeakand;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

import java.io.Serializable;

import io.github.yzernik.squeaklib.core.Signing;


@Entity(tableName = TodoRoomDatabase.TABLE_NAME_PROFILE)
@TypeConverters({Converters.class})
public class SqueakProfile implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int profile_id;

    public String name;

    public ECKey ecKey;

    public SqueakProfile() {
    }

    @Ignore
    public SqueakProfile(String name, ECKey ecKey) {
        this.name = name;
        this.ecKey = ecKey;
    }

    @Ignore
    public String getName() {
        return name;
    }

    @Ignore
    public ECKey getEcKey() {
        return ecKey;
    }

    @Ignore
    public Signing.KeyPair getKeyPair() {
        return new Signing.BitcoinjKeyPair(ecKey);
    }

    @Ignore
    public String  getAddress() {
        return getAddress(MainNetParams.get());
    }

    @Ignore
    public String getAddress(NetworkParameters params) {
        return getKeyPair().getPublicKey().getAddress(params);
    }

    @Ignore
    public int getProfileId() {
        return profile_id;
    }

    @Override
    public String toString() {
        return "String("
                + "name: " + name + ", "
                + "profile_id: " + profile_id + ", "
                + "address: " + getAddress()
                + ")";
    }

}