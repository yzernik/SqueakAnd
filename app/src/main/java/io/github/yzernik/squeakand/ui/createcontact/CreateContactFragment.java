package io.github.yzernik.squeakand.ui.createcontact;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import org.bitcoinj.core.LegacyAddress;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.networkparameters.NetworkParameters;

public class CreateContactFragment extends Fragment {

    TextInputLayout mContactNameInput;
    TextInputLayout mContactAddressInput;
    Button mGetAddressFromQRCodeButton;
    Button mCreateContactButton;

    private CreateContactModel createContactModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_contact, container, false);

        mContactNameInput = root.findViewById(R.id.new_contact_name_input);
        mContactAddressInput = root.findViewById(R.id.new_contact_address_input);
        mGetAddressFromQRCodeButton = root.findViewById(R.id.contact_address_from_qr_code_button);
        mCreateContactButton = root.findViewById(R.id.create_contact_finish_button);

        // Start the fragment with the contact name input in focus.
        mContactNameInput.requestFocus();

        createContactModel = new ViewModelProvider(getActivity()).get(CreateContactModel.class);

        // TODO: Implement an activity to read QR code an fill in address input field with the result.
        mGetAddressFromQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getTag(), "Import private key here.");
                showUnsupportedQRReadAlert();
            }
        });

        // Change the button response
        mCreateContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactName = mContactNameInput.getEditText().getText().toString();
                String contactAddress = mContactAddressInput.getEditText().getText().toString();

                if (contactName.isEmpty()) {
                    showMissingNameAlert();
                    return;
                }

                if (contactAddress.isEmpty()) {
                    showMissingAddressAlert();
                    return;
                }

                if (!isValidAddress(contactAddress)) {
                    showInvalidAddressAlert();
                    return;
                }

                Log.i(getTag(), "Creating contact here.");
                SqueakProfile squeakProfile = new SqueakProfile(contactName, contactAddress);
                createContactModel.insert(squeakProfile);
                getActivity().finish();
            }
        });


        return root;
    }


    private void showMissingNameAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Missing contact name");
        alertDialog.setMessage("Contact name cannot be empty.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showMissingAddressAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Missing contact address");
        alertDialog.setMessage("Contact address cannot be empty.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showInvalidAddressAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Invalid contact address");
        alertDialog.setMessage("Invalid address entered.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showUnsupportedQRReadAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("QR code reader not supported");
        alertDialog.setMessage("Reading address from QR code is not supported yet.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private boolean isValidAddress(String addressString) {
        try {
            LegacyAddress.fromBase58(NetworkParameters.getNetworkParameters(), addressString);
            return true;
        } catch (IllegalArgumentException e) {
            Log.e(getTag(), "Invalid address: " + addressString);
            return false;
        }
    }

}
