package io.github.yzernik.squeakand.ui.createtodo;

import android.view.View;
import android.widget.AdapterView;

import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.ui.selectprofile.SelectProfileModel;

public class ProfileSelectedListener implements AdapterView.OnItemSelectedListener{

    private SelectProfileModel selectProfileModel;

    public ProfileSelectedListener(SelectProfileModel selectProfileModel) {
        this.selectProfileModel = selectProfileModel;
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
