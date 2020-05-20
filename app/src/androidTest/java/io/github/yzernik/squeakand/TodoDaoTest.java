package io.github.yzernik.squeakand;

/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4.class)
public class TodoDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TodoDao mTodoDao;
    private SqueakRoomDatabase mDb;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        mDb = Room.inMemoryDatabaseBuilder(context, SqueakRoomDatabase.class)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build();
        mTodoDao = mDb.todoDao();
    }

    @After
    public void closeDb() {
        mDb.close();
    }

    @Test
    public void insertAndGetTodo() throws Exception {
        Todo todo = new Todo("todo");
        mTodoDao.insert(todo);
        List<Todo> allTodos = LiveDataTestUtil.getValue(mTodoDao.getAlphabetizedTodos());
        assertEquals(allTodos.get(0).getName(), todo.getName());
    }

    @Test
    public void getAllTodos() throws Exception {
        Todo todo = new Todo("aaa");
        mTodoDao.insert(todo);
        Todo todo2 = new Todo("bbb");
        mTodoDao.insert(todo2);
        List<Todo> allTodos = LiveDataTestUtil.getValue(mTodoDao.getAlphabetizedTodos());
        assertEquals(allTodos.get(0).getName(), todo.getName());
        assertEquals(allTodos.get(1).getName(), todo2.getName());
    }

    @Test
    public void deleteAll() throws Exception {
        Todo todo = new Todo("todo");
        mTodoDao.insert(todo);
        Todo todo2 = new Todo("todo2");
        mTodoDao.insert(todo2);
        mTodoDao.deleteAll();
        List<Todo> allTodos = LiveDataTestUtil.getValue(mTodoDao.getAlphabetizedTodos());
        assertTrue(allTodos.isEmpty());
    }
}
