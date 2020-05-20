package io.github.yzernik.squeakand;



import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = SqueakRoomDatabase.TABLE_NAME_TODO)
public class Todo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int todo_id;

    public String name;

    public String description;

    public String category;

    @Ignore
    public String priority;

    public Todo() {
    }

    @Ignore
    public Todo(String description) {
        this.description = description;
    }

    @Ignore
    public String getName() {
        return name;
    }

}