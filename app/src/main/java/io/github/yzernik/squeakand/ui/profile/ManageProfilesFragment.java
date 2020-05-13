package io.github.yzernik.squeakand.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;


public class ManageProfilesFragment extends Fragment {

    private SelectProfileModel selectProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        selectProfileModel =
                ViewModelProviders.of(this).get(SelectProfileModel.class);
        View root = inflater.inflate(R.layout.fragment_manage_profiles, container, false);

        return root;
    }

}
