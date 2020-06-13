package io.github.yzernik.squeakand.ui.createsqueak;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import io.github.yzernik.squeakand.ElectrumActivity;
import io.github.yzernik.squeakand.ManageProfilesActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.blockchain.BlockInfo;
import io.github.yzernik.squeakand.blockchain.ServerUpdate;
import io.github.yzernik.squeakand.networkparameters.NetworkParameters;
import io.github.yzernik.squeaklib.core.Squeak;


public class CreateSqueakFragment extends Fragment {

    private static final int MAXIMUM_SQUEAK_TEXT_LENGTH = 280;

    private Button mSelectProfileButton;
    private Button mLatestBlockHeightButton;
    private TextInputLayout mTextInput;
    private Button button;

    private CreateSqueakModel createSqueakModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_squeak, container, false);

        mSelectProfileButton = root.findViewById(R.id.select_profile_button);
        mLatestBlockHeightButton = root.findViewById(R.id.latest_block_height_button);
        mTextInput = root.findViewById(R.id.squeak_text);
        button = root.findViewById(R.id.btnDone);

        // Start the fragment with the text input in focus.
        mTextInput.requestFocus();

        createSqueakModel = new ViewModelProvider(getActivity()).get(CreateSqueakModel.class);

        createSqueakModel.getSelectedSqueakProfile().observe(getViewLifecycleOwner(), new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable final SqueakProfile squeakProfile) {
                // set the textview to show the currently selected profile.
                if (squeakProfile != null) {
                    updateDisplayedProfile(squeakProfile);
                }
            }
        });

        createSqueakModel.getmAllSqueakSigningProfiles().observe(getViewLifecycleOwner(), new Observer<List<SqueakProfile>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakProfile> profiles) {
                mSelectProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProfileAlertDialog(profiles);
                    }
                });
            }
        });

        createSqueakModel.getServerUpdate().observe(getViewLifecycleOwner(), new Observer<ServerUpdate>() {
            @Override
            public void onChanged(@Nullable final ServerUpdate serverUpdate) {
                ServerUpdate.ConnectionStatus connectionStatus = serverUpdate.getConnectionStatus();
                switch (connectionStatus) {
                    case CONNECTED:
                        mLatestBlockHeightButton.setText(serverUpdate.getElectrumServerAddress().toString());
                        break;
                    default:
                        mLatestBlockHeightButton.setText("Disconnected");
                        break;
                }
                mLatestBlockHeightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showElectrumAlertDialog(serverUpdate);
                    }
                });
            }
        });

        createSqueakModel.getCreateSqueakParams().observe(getViewLifecycleOwner(), new Observer<CreateSqueakParams>() {
            @Override
            public void onChanged(@Nullable final CreateSqueakParams createSqueakParams) {
                Log.i(getTag(), "Observed new create squeak params: " + createSqueakParams);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        String inputText = mTextInput.getEditText().getText().toString();
                        createSqueak(createSqueakParams, inputText);
                    }
                });
            }
        });

        return root;
    }


    /**
     * Show the alert dialog for selecting a profile.
     * @param profiles
     */
    private void showProfileAlertDialog(List<SqueakProfile> profiles) {
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
                createSqueakModel.setSelectedSqueakProfileId(selectedProfile.getProfileId());
            }
        });

        // Add the manage profiles button
        builder.setNeutralButton("Manage profiles", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Toast.makeText(getContext(), "neutralize", Toast.LENGTH_SHORT).show();
                System.out.println("Manage profiles button clicked");
                startManageProfiles();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Show the alert dialog for the electrum connection.
     * @param serverUpdate
     */
    private void showElectrumAlertDialog(ServerUpdate serverUpdate) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Electrum connection");

        switch (serverUpdate.getConnectionStatus()) {
            case CONNECTED:
                String serverString = serverUpdate.getElectrumServerAddress().toString();
                BlockInfo blockInfo = serverUpdate.getBlockInfo();
                builder.setMessage("Connected to: " + serverString + " with block height: " + blockInfo.getHeight());
                break;
            default:
                builder.setMessage("Not connected to any electrum server.");
                break;
        }
        // Add the manage electrum button
        builder.setNeutralButton("Manage electrum connection", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Toast.makeText(getContext(), "neutralize", Toast.LENGTH_SHORT).show();
                System.out.println("Manage electrum button clicked");
                startManageElectrum();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Update the display with the given profile
     * @param squeakProfile
     */
    private void updateDisplayedProfile(SqueakProfile squeakProfile) {
        mSelectProfileButton.setText(squeakProfile.getName());
    }

    public void startManageProfiles() {
        Intent intent = new Intent(getActivity(), ManageProfilesActivity.class);
        startActivity(intent);
    }

    public void startManageElectrum() {
        Intent intent = new Intent(getActivity(), ElectrumActivity.class);
        startActivity(intent);
    }

    private void createSqueak(CreateSqueakParams createSqueakParams, String squeakText) {
        if (TextUtils.isEmpty(squeakText)) {
            showEmptyTextAlert();
            return;
        }

        if (squeakText.length() > MAXIMUM_SQUEAK_TEXT_LENGTH) {
            showTooLongTextAlert();
            return;
        }

        SqueakProfile squeakProfile = createSqueakParams.getSqueakProfile();
        if (squeakProfile == null) {
            showMissingProfileAlert();
            return;
        }

        BlockInfo blockTip = createSqueakParams.getLatestBlockk();
        if (blockTip == null) {
            showMissingBlockHeaderAlert();
            return;
        }

        try {
            Squeak squeak = Squeak.makeSqueakFromStr(
                    NetworkParameters.getNetworkParameters(),
                    createSqueakParams.getSqueakProfile().getKeyPair(),
                    squeakText,
                    createSqueakParams.getLatestBlockk().getHeight(),
                    createSqueakParams.getLatestBlockk().getHash(),
                    System.currentTimeMillis() / 1000,
                    createSqueakParams.getReplyToHash()
            );
            createSqueakModel.insertSqueak(squeak);
            Log.i(getTag(), "Created and inserted squeak: " + squeak);
            Log.i(getTag(), "Created squeak with content: " + squeak.getDecryptedContentStr());

            // Upload the squeak to the servers
            createSqueakModel.uploadSqueak(squeak);
            Log.i(getTag(), "Enqueued squeak to upload.");

            getActivity().finish();
        } catch (Exception e) {
            Log.e(getTag(), "Unable to create squeak: " + e);
        }
    }


    private void showMissingProfileAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Missing profile");
        alertDialog.setMessage("Profile must be selected before a squeak can be created.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showMissingBlockHeaderAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Missing Block Header");
        alertDialog.setMessage("Latest block header must be downloaded before a squeak can be created.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showEmptyTextAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Empty text");
        alertDialog.setMessage("Text cannot be empty.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showTooLongTextAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Text input too long");
        alertDialog.setMessage("Text cannot be more than " + MAXIMUM_SQUEAK_TEXT_LENGTH  + " characters.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
