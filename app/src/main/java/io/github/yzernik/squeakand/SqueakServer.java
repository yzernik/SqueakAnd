package io.github.yzernik.squeakand;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;

import io.github.yzernik.squeakand.server.SqueakServerAddress;

@Entity(
        tableName = SqueakRoomDatabase.TABLE_NAME_SERVER,
        indices = {@Index(value = {"serverAddress"}, unique = true)})
@TypeConverters({Converters.class})
public class SqueakServer implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int server_id;

    public String serverName;

    @NonNull
    public SqueakServerAddress serverAddress;

}
