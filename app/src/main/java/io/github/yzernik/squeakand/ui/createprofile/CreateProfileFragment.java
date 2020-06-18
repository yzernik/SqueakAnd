package io.github.yzernik.squeakand.ui.createprofile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import org.bitcoinj.core.ECKey;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.networkparameters.NetworkParameters;
import io.github.yzernik.squeakand.server.SqueakServerAddress;
import io.github.yzernik.squeaklib.core.Signing;

import static org.bitcoinj.core.Utils.HEX;

public class CreateProfileFragment extends Fragment {

    TextInputLayout mProfileNameInput;
    TextInputLayout mProfileAddressDisplay;
    Button mGeneratePrivateKeyButton;
    Button mImportPrivateKeyButton;
    Button mCreateProfileButton;

    private CreateProfileModel createProfileModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_profile, container, false);

        mProfileNameInput = root.findViewById(R.id.new_profile_name_input);
        mProfileAddressDisplay = root.findViewById(R.id.new_profile_address_display);
        mGeneratePrivateKeyButton = root.findViewById(R.id.create_profile_generate_private_key_button);
        mImportPrivateKeyButton = root.findViewById(R.id.create_profile_import_private_key_button);
        mCreateProfileButton = root.findViewById(R.id.create_profile_finish_button);

        // Start the fragment with the profile name input in focus.
        mProfileNameInput.requestFocus();

        createProfileModel = new ViewModelProvider(getActivity()).get(CreateProfileModel.class);

        mGeneratePrivateKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getTag(), "Generating key pair");
                generateKeyPair();
            }
        });

        // Show an alert dialog when the import private key button is clicked.
        mImportPrivateKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getTag(), "Import private key here.");
                showImportPrivateKeyAlertDialog(inflater);
            }
        });

        createProfileModel.getKeyPair().observe(getActivity(), new Observer<Signing.BitcoinjKeyPair>() {
            @Override
            public void onChanged(@Nullable final Signing.BitcoinjKeyPair keyPair) {
                Log.i(getTag(), "Keypair change observed.");

                // Change the address display
                if (keyPair != null) {
                    mProfileAddressDisplay.setHint(keyPair.getPublicKey().getAddress(NetworkParameters.getNetworkParameters()));
                }

                // Change the button response
                mCreateProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (keyPair == null) {
                            showMissingKeyAlert();
                            return;
                        }

                        Log.i(getTag(), "Creating profile here.");
                        String profileName = mProfileNameInput.getEditText().getText().toString();
                        SqueakProfile squeakProfile = new SqueakProfile(profileName, keyPair);
                        createProfileModel.insert(squeakProfile);
                        getActivity().finish();
                    }
                });
            }
        });



        return root;
    }

    private void generateKeyPair() {
        Log.i(getTag(), "Generate private key here.");
        ECKey ecKey = new ECKey();
        // SqueakProfile squeakProfile = new SqueakProfile(profileName, ecKey);
        // createProfileModel.insert(squeakProfile);
        // Toast.makeText(getContext(), "Created profile " + profileName, Toast.LENGTH_SHORT).show();
        createProfileModel.setmKeyPair(ecKey);
    }

    private void showUnsupportedImportAlert() {
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

    private void showMissingKeyAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Missing key pair.");
        alertDialog.setMessage("Key pair must be generated or imported before a profile can be created.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showImportPrivateKeyAlertDialog(LayoutInflater inflater) {
        final View view = inflater.inflate(R.layout.dialog_import_private_key, null);
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Import private key");

        final EditText privateKeyEditText = (EditText) view.findViewById(R.id.dialog_import_private_key_key_input);

        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Import",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String privateKeyString = privateKeyEditText.getText().toString();
                        if (privateKeyString == null || privateKeyString.isEmpty()) {
                            dialog.dismiss();
                            showInvalidPrivateKeyAlert();
                            return;
                        }

                        try {
                            byte[] privKeyBytes = HEX.decode(privateKeyString);
                            ECKey ecKey = ECKey.fromPrivate(privKeyBytes);
                            Log.i(getTag(), "Setting new private key");
                            createProfileModel.setmKeyPair(ecKey);
                        } catch (Exception e) {
                            dialog.dismiss();
                            showInvalidPrivateKeyAlert();
                            return;
                        }
                    }
                });

        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setView(view);
        alertDialog.show();
    }

    private void showInvalidPrivateKeyAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Invalid private key");
        alertDialog.setMessage("Imported private key is invalid.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
