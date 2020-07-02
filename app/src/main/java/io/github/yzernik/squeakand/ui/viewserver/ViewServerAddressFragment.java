package io.github.yzernik.squeakand.ui.viewserver;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.server.SqueakServerAddress;

public class ViewServerAddressFragment extends Fragment {

    private TextView serverAddressTextView;
    private TextView serverNameTextView;
    private Button createServerButton;
    private Button editServerButton;
    private FrameLayout missingServerBanner;
    private FrameLayout presentServerBanner;

    private ViewServerAddressModel viewServerAddressModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_server_address, container, false);

        String squeakServerAddressString = this.getArguments().getString("squeak_server_address");
        SqueakServerAddress squeakServerAddress = SqueakServerAddress.fromString(squeakServerAddressString);

        serverAddressTextView = root.findViewById(R.id.server_address_string_text);
        missingServerBanner = root.findViewById(R.id.server_address_profile_missing_layout);
        presentServerBanner = root.findViewById(R.id.server_address_profile_present_layout);
        serverNameTextView = root.findViewById(R.id.server_address_profile_name_text);
        createServerButton = root.findViewById(R.id.server_address_create_profile_button);
        editServerButton = root.findViewById(R.id.server_address_edit_profile_button);

        viewServerAddressModel = ViewModelProviders.of(this,
                new ViewServerAddressModelFactory(getActivity().getApplication(), squeakServerAddress))
                .get(ViewServerAddressModel.class);

        serverAddressTextView.setText(squeakServerAddress.toString());

        return root;
    }


}