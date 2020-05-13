package io.github.yzernik.squeakand.ui.profile;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;

public class SelectProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Button mSelectProfileButton;
    private TextView mSelectedProfileText;

    private SelectProfileModel selectProfileModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);
        selectProfileModel =
                ViewModelProviders.of(this).get(SelectProfileModel.class);
        mSelectProfileButton = findViewById(R.id.select_profile_button);
        mSelectedProfileText = findViewById(R.id.selected_profile_text);

        selectProfileModel.getSelectedSqueakProfile().observe(this, new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable final SqueakProfile squeakProfile) {
                // set the textview to show the currently selected profile.
                if (squeakProfile != null) {
                    mSelectedProfileText.setText(squeakProfile.getName());
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SqueakProfile profile = (SqueakProfile) parent.getItemAtPosition(position);
        selectProfileModel.setSelectedSqueakProfileId(profile.profile_id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }

}
