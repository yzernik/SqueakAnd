package io.github.yzernik.squeakand.ui.createprofile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;

import java.util.List;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeaklib.core.Signing;

public class CreateProfileFragment extends Fragment implements GeneratePrivateKeyDialogFragment.GeneratePrivateKeyNoticeDialogListener {

    TextInputLayout mProfileNameInput;
    Button mGeneratePrivateKeyButton;
    Button mImportPrivateKeyButton;
    TextView mProfileAddress;

    private CreateProfileModel createProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_profile, container, false);

        mProfileNameInput = root.findViewById(R.id.new_profile_name_input);
        mGeneratePrivateKeyButton = root.findViewById(R.id.create_profile_generate_private_key_button);
        mImportPrivateKeyButton = root.findViewById(R.id.create_profile_import_private_key_button);
        mProfileAddress = root.findViewById(R.id.new_profile_address);

        createProfileModel = new ViewModelProvider(getActivity()).get(CreateProfileModel.class);

        DialogFragment dialog = new GeneratePrivateKeyDialogFragment("", this);

        mGeneratePrivateKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show(getChildFragmentManager(), "FireMissilesDialogFragment");
            }
        });

        // Show an alert dialog when the import private key button is clicked.
        mImportPrivateKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getTag(), "Import private key here.");
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Import private key not supported.");
                alertDialog.setMessage("Importing private keys is not currently supported.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        createProfileModel.getKeyPair().observe(getActivity(), new Observer<Signing.KeyPair>() {
            @Override
            public void onChanged(@Nullable final Signing.KeyPair keyPair) {
                mProfileAddress.setText(keyPair.getPublicKey().getAddress(MainNetParams.get()));
            }
        });


        return root;
    }

    private void generateKeyPair(String profileName) {
        Log.i(getTag(), "Generate private key here.");
        ECKey ecKey = new ECKey();
        // SqueakProfile squeakProfile = new SqueakProfile(profileName, ecKey);
        // createProfileModel.insert(squeakProfile);
        // Toast.makeText(getContext(), "Created profile " + profileName, Toast.LENGTH_SHORT).show();
        createProfileModel.setmKeyPair(ecKey);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String profileName) {
        generateKeyPair(profileName);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, String profileName) {
        // Do nothing.
    }

}
