package io.github.yzernik.squeakand.ui.viewcontact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.ui.createcontact.CreateContactModel;

public class ViewContactFragment extends Fragment {

    private TextView mContactNameText;
    private TextView mContactAddressText;

    private ViewContactModel viewContactModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_contact, container, false);

        int profileId = this.getArguments().getInt("profile_id", -1);

        mContactNameText = root.findViewById(R.id.view_contact_show_name);
        mContactAddressText = root.findViewById(R.id.view_contact_show_address);

        viewContactModel = new ViewModelProvider(getActivity()).get(ViewContactModel.class);

        viewContactModel.getSqueakContactProfile(profileId).observe(getViewLifecycleOwner(), new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable SqueakProfile squeakProfile) {
                if (squeakProfile == null) {
                    return;
                }

                mContactNameText.setText(squeakProfile.getName());
                mContactAddressText.setText(squeakProfile.getAddress());
            }
        });


        return root;
    }


}
