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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.TodoViewModel;

/**
 * Activity for entering a word.
 */

public class ViewTodoActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "io.github.yzernik.squeakand.REPLY";

    TextView txtName;
    TextView txtNo;
    TextView txtDesc;
    TextView txtCategory;
    CardView cardView;

    // private EditText mEditTodoView;
    private TodoViewModel todoViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_todo);
        // mEditTodoView = findViewById(R.id.inTitle);

        // Get the transferred data from source activity.
        int todoId = getIntent().getIntExtra("id", 0);

        // int todoId = savedInstanceState.getInt("todo_id");
        // int todoId = this.getArguments().getInt("todo_id");
        System.out.println("todoId: " + todoId);

        // Get a new or existing ViewModel from the ViewModelProvider.
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        txtNo = findViewById(R.id.txtNo);
        txtName = findViewById(R.id.txtName);
        txtDesc = findViewById(R.id.txtDesc);
        txtCategory = findViewById(R.id.txtCategory);
        cardView = findViewById(R.id.cardView);


/*        final Button button = findViewById(R.id.btnDone);
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
        });*/

        todoViewModel.getSingleTodo(todoId).observe(this, new Observer<Todo>() {
            @Override
            public void onChanged(@Nullable Todo todo) {
                System.out.println("Handling onChanged: " + todo);
                if (todo == null) {
                    return;
                }

                System.out.println("Setting layout to show todo: " + todo);
                txtName.setText(todo.getName());
                txtNo.setText("#" + String.valueOf(todo.todo_id));
                txtDesc.setText(todo.description);
                txtCategory.setText(todo.category);
            }
        });
    }
}
