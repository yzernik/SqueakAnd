package io.github.yzernik.squeakand;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;

import io.github.yzernik.squeakand.server.SqueakServerAddress;
import io.github.yzernik.squeaklib.core.Signing;

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

    public SqueakServer() {
    }

    @Ignore
    public SqueakServer(String serverName, SqueakServerAddress serverAddress) {
        this.serverName = serverName;
        this.serverAddress = serverAddress;
    }

    @Ignore
    public int getId() {
        return server_id;
    }

    @Ignore
    public String getName() {
        return serverName;
    }

    @Ignore
    public SqueakServerAddress getAddress() {
        return serverAddress;
    }

}
