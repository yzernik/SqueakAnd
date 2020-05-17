package io.github.yzernik.squeakand.ui.createtodo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SelectProfileActivity;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.ui.profile.SelectProfileModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CreateTodoFragment extends Fragment {

    public static final String EXTRA_REPLY = "io.github.yzernik.squeakand.REPLY";

    private EditText mEditTodoView;
    private Button button;
    private Button selectProfileButton;
    private TextView currentProfileText;

    private SelectProfileModel selectProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_todo, container, false);

        mEditTodoView = root.findViewById(R.id.inTitle);
        currentProfileText = root.findViewById(R.id.new_todo_current_profile_text);
        button = root.findViewById(R.id.btnDone);
        selectProfileButton = root.findViewById(R.id.new_todo_select_profile_button);

        selectProfileModel =
                ViewModelProviders.of(this).get(SelectProfileModel.class);


        selectProfileModel.getSelectedSqueakProfile().observe(getViewLifecycleOwner(), new Observer<SqueakProfile>() {
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
                Intent intent = new Intent(getContext(), SelectProfileActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(getTag(), "Button clicked");
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditTodoView.getText())) {
                    getActivity().setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String word = mEditTodoView.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY, word);
                    getActivity().setResult(RESULT_OK, replyIntent);
                    Log.i(getTag(), "Set result for activity: " + getTag());
                }
                Log.i(getTag(), "Finishing activity: " + getActivity());
                getActivity().finish();
            }
        });


        return root;
    }

}
