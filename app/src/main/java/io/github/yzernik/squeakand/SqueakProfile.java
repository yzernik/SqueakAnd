package io.github.yzernik.squeakand;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = TodoRoomDatabase.TABLE_NAME_PROFILE)
public class SqueakProfile implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int profile_id;

    public String name;

    public SqueakProfile() {
    }

    @Ignore
    public SqueakProfile(String name) {
        this.name = name;
    }

    @Ignore
    public String getName() {
        return name;
    }

}