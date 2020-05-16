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
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.todo.ViewTodoFragment;

/**
 * Activity for entering a word.
 */

public class ViewTodoActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "io.github.yzernik.squeakand.REPLY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_todo);

        // Get the transferred data from source activity.
        int todoId = getIntent().getIntExtra("id", 0);


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


        Log.i(getCallingPackage(), "todoId in onCreate: " + todoId);

        Bundle bundle = new Bundle();
        bundle.putInt("todo_id", todoId);
        // Create new fragment and transaction
        Fragment newFragment = new ViewTodoFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        newFragment.setArguments(bundle);
        // int currentContainerViewId = ((ViewGroup) getView().getParent()).getId();
        transaction.replace(R.id.your_placeholder, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

}
