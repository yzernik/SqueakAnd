package io.github.yzernik.squeakand;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.  In a real
 * app, consider exporting the schema to help you with migrations.
 */

@Database(entities = {Todo.class, SqueakProfile.class}, version = 1, exportSchema = false)
abstract class TodoRoomDatabase extends RoomDatabase {

    public static final String DB_NAME = "app_db";
    public static final String TABLE_NAME_TODO = "todo";
    public static final String TABLE_NAME_PROFILE = "profile";

    abstract TodoDao todoDao();
    abstract SqueakProfileDao squeakProfileDao();


    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile TodoRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TodoRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TodoRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TodoRoomDatabase.class, DB_NAME)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Override the onOpen method to populate the database.
     * For this sample, we clear the database every time it is created or opened.
     *
     * If you want to populate the database only when the database is created for the 1st time,
     * override RoomDatabase.Callback()#onCreate
     */
    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more todos, just add them.
                TodoDao dao = INSTANCE.todoDao();
                dao.deleteAll();

                for (Todo todo: INSTANCE.buildDummyTodos()) {
                    dao.insert(todo);
                }

                SqueakProfileDao squeakProfileDao = INSTANCE.squeakProfileDao();
                squeakProfileDao.deleteAll();

/*                for (SqueakProfile squeakProfile: INSTANCE.buildDummySqueakProfiles()) {
                    squeakProfileDao.insert(squeakProfile);
                }*/
            });
        }
    };

    private List<Todo> buildDummyTodos() {
        List<Todo> todoArrayList = new ArrayList<>();
        Todo todo = new Todo();
        todo.name = "Android Retrofit Tutorial";
        todo.description = "Cover a tutorial on the Retrofit networking library using a RecyclerView to show the data.";
        todo.category = "Android";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "iOS TableView Tutorial";
        todo.description = "Covers the basics of TableViews in iOS using delegates.";
        todo.category = "iOS";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "Kotlin Arrays";
        todo.description = "Cover the concepts of Arrays in Kotlin and how they differ from the Java ones.";
        todo.category = "Kotlin";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "Swift Arrays";
        todo.description = "Cover the concepts of Arrays in Swift and how they differ from the Java and Kotlin ones.";
        todo.category = "Swift";

        todoArrayList.add(todo);

        return todoArrayList;
    }


    private List<SqueakProfile> buildDummySqueakProfiles() {
        List<SqueakProfile> squeakProfileArrayList = new ArrayList<>();
        SqueakProfile squeakProfile = new SqueakProfile();
        squeakProfile.name = "profile1";

        squeakProfileArrayList.add(squeakProfile);

        squeakProfile = new SqueakProfile();
        squeakProfile.name = "profile2";

        squeakProfileArrayList.add(squeakProfile);

        squeakProfile = new SqueakProfile();
        squeakProfile.name = "profile3";

        squeakProfileArrayList.add(squeakProfile);

        return squeakProfileArrayList;
    }

}
