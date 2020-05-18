package io.github.yzernik.squeakand.ui.createtodo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import io.github.yzernik.squeakand.ManageProfilesActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SelectProfileActivity;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.ui.profile.SelectProfileModel;
import io.github.yzernik.squeakand.ui.profile.SharedViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CreateTodoFragment extends Fragment {

    public static final String EXTRA_REPLY = "io.github.yzernik.squeakand.REPLY";

    private EditText mEditTodoView;
    private Button button;
    private Button otherSelectProfileButton;
    private TextView currentProfileText;
    private TextView mObservedSharedText;

    private SelectProfileModel selectProfileModel;
    private SharedViewModel sharedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_todo, container, false);

        mEditTodoView = root.findViewById(R.id.inTitle);
        currentProfileText = root.findViewById(R.id.new_todo_current_profile_text);
        button = root.findViewById(R.id.btnDone);
        otherSelectProfileButton = root.findViewById(R.id.other_select_profile_button);
        mObservedSharedText = root.findViewById(R.id.observed_shared_text);

        selectProfileModel = new ViewModelProvider(getActivity()).get(SelectProfileModel.class);
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        Log.i(getTag(), "Created sharedViewModel: " + sharedViewModel + "in CreateTodoFragment");

        selectProfileModel.getSelectedSqueakProfile().observe(getViewLifecycleOwner(), new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable final SqueakProfile squeakProfile) {
                Log.i(getTag(), "Updating CreateTodoFragment display with profile: " + squeakProfile);
                // set the textview to show the currently selected profile.
                if (squeakProfile != null) {
                    currentProfileText.setText(squeakProfile.getName());
                }
            }
        });

        selectProfileModel.getmAllSqueakProfiles().observe(getViewLifecycleOwner(), new Observer<List<SqueakProfile>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakProfile> profiles) {
                otherSelectProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(getTag(),"Select profile button clicked");
                        showAlertDialog(profiles);
                    }
                });
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
                    Log.i(getTag(), "Set result for activity: " + word);
                }
                Log.i(getTag(), "Finishing activity: " + getActivity());
                getActivity().finish();
            }
        });

        sharedViewModel.getSelected().observe(getViewLifecycleOwner(), sharedTextString -> {
            Log.i(getTag(), "Observed mObservedSharedText in CreateTodoFragment");
            mObservedSharedText.setText(sharedTextString);
        });


        return root;
    }


    /**
     * Show the alert dialog for selecting a profile.
     * @param profiles
     */
    private void showAlertDialog(List<SqueakProfile> profiles) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose a profile");
        // add a list
        ArrayList<String> displayValues=new ArrayList<>();
        for (SqueakProfile profile : profiles) {
            displayValues.add(profile.getName());
        }
        String[] displayValuesArr = displayValues.toArray(new String[displayValues.size()]);
        builder.setItems(displayValuesArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SqueakProfile selectedProfile = profiles.get(which);
                selectProfileModel.setSelectedSqueakProfileId(selectedProfile.getProfileId());
                Toast.makeText(getContext(), "Selected profile " + selectedProfile.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
