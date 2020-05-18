package io.github.yzernik.squeakand.ui.createprofile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.bitcoinj.core.ECKey;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.ui.selectprofile.SelectProfileModel;

public class CreateProfileFragment extends Fragment implements GeneratePrivateKeyDialogFragment.GeneratePrivateKeyNoticeDialogListener {

    TextView mShowNewProfileName;
    Button mGeneratePrivateKeyButton;
    Button mImportPrivateKeyButton;

    private SelectProfileModel selectProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_profile, container, false);

        mShowNewProfileName = root.findViewById(R.id.create_profile_new_profile_name);
        mGeneratePrivateKeyButton = root.findViewById(R.id.create_profile_generate_private_key_button);
        mImportPrivateKeyButton = root.findViewById(R.id.create_profile_import_private_key_button);

        // Get a new or existing ViewModel from the ViewModelProvider.
        selectProfileModel = new ViewModelProvider(this).get(SelectProfileModel.class);

        // String profileName = "";
        Bundle arguments = getArguments();
        final String profileName = this.getArguments().getString("name");

        DialogFragment dialog = new GeneratePrivateKeyDialogFragment(profileName, this);

        mShowNewProfileName.setText(profileName);

        mGeneratePrivateKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show(getChildFragmentManager(), "FireMissilesDialogFragment");
            }
        });

        mImportPrivateKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getTag(), "Import private key here.");
            }
        });


        return root;
    }

    private void createProfileWithPrivateKey(String profileName) {
        Log.i(getTag(), "Generate private key here.");
        ECKey ecKey = new ECKey();
        SqueakProfile squeakProfile = new SqueakProfile(profileName, ecKey);
        selectProfileModel.insert(squeakProfile);
        Toast.makeText(getContext(), "Created profile " + profileName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String profileName) {
        createProfileWithPrivateKey(profileName);
        getActivity().finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String profileName) {
        // Do nothing.
    }

}
