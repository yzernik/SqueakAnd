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

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.createtodo.CreateTodoFragment;
import io.github.yzernik.squeakand.ui.selectprofile.SelectProfileFragment;

/**
 * Activity for entering a word.
 */

public class NewTodoActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "io.github.yzernik.squeakand.REPLY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment selectProfileFragment = new SelectProfileFragment();
        transaction.replace(R.id.select_profile_fragment_frame, selectProfileFragment);
        Fragment createTodoFragment = new CreateTodoFragment();
        transaction.replace(R.id.create_todo_fragment_frame, createTodoFragment);
        transaction.commit();
    }
}
