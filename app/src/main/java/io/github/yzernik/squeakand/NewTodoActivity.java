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

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.ui.profile.SelectProfileActivity;
import io.github.yzernik.squeakand.ui.profile.SelectProfileModel;

/**
 * Activity for entering a word.
 */

public class NewTodoActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "io.github.yzernik.squeakand.REPLY";

    private EditText mEditTodoView;
    private Button button;
    private Button selectProfileButton;
    private TextView currentProfileText;

    private SelectProfileModel selectProfileModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);
        mEditTodoView = findViewById(R.id.inTitle);
        currentProfileText = findViewById(R.id.new_todo_current_profile_text);
        button = findViewById(R.id.btnDone);
        selectProfileButton = findViewById(R.id.new_todo_select_profile_button);

        selectProfileModel =
                ViewModelProviders.of(this).get(SelectProfileModel.class);

        selectProfileModel.getSelectedSqueakProfile().observe(this, new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable final SqueakProfile squeakProfile) {
                // set the textview to show the currently selected profile.
                if (squeakProfile != null) {
                    currentProfileText.setText(squeakProfile.getName());
                }
            }
        });

        selectProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Select profile button clicked");
                Intent intent = new Intent(getApplication(), SelectProfileActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("Button clicked");
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditTodoView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String word = mEditTodoView.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY, word);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}
