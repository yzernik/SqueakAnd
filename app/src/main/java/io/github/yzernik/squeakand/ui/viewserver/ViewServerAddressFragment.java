package io.github.yzernik.squeakand.ui.viewserver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;
import java.util.stream.Collectors;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.ViewServerActivity;
import io.github.yzernik.squeakand.server.SqueakServerAddress;

public class ViewServerAddressFragment extends Fragment {

    private TextView serverAddressTextView;
    private TextView serverNameTextView;
    private Button createServerButton;
    private Button editServerButton;
    private FrameLayout missingServerBanner;
    private FrameLayout presentServerBanner;
    private TextView serverPaidOffersText;
    private TextView serverValidPaidOffersText;
    private TextView serverInvalidPaidOffersText;

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
        serverPaidOffersText = root.findViewById(R.id.server_address_paid_offers_text);
        serverValidPaidOffersText = root.findViewById(R.id.server_address_valid_paid_offers_text);
        serverInvalidPaidOffersText = root.findViewById(R.id.server_address_invalid_paid_offers_text);

        viewServerAddressModel = ViewModelProviders.of(this,
                new ViewServerAddressModelFactory(getActivity().getApplication(), squeakServerAddress))
                .get(ViewServerAddressModel.class);

        serverAddressTextView.setText(squeakServerAddress.toString());

        viewServerAddressModel.getLiveSqueakServer().observe(getViewLifecycleOwner(), new Observer<SqueakServer>() {
            @Override
            public void onChanged(@Nullable final SqueakServer squeakServer) {
                if (squeakServer == null) {
                    presentServerBanner.setVisibility(View.GONE);
                    missingServerBanner.setVisibility(View.VISIBLE);
                    createServerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: create a new server database entry for server address.
                            // startActivity(new Intent(getActivity(), NewServerActivity.class).putExtra("squeak_address", squeakAddress));
                        }
                    });

                } else {
                    missingServerBanner.setVisibility(View.GONE);
                    presentServerBanner.setVisibility(View.VISIBLE);
                    serverNameTextView.setText(squeakServer.getName());
                    editServerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), ViewServerActivity.class).putExtra("server_id", squeakServer.getId()));
                        }
                    });
                }

            }
        });

        viewServerAddressModel.getLivePaidOffers().observe(getViewLifecycleOwner(), new Observer<List<Offer>>() {
            @Override
            public void onChanged(@Nullable final List<Offer> paidOffers) {
                List<Offer> validPaidOffers = paidOffers.stream()
                        .filter(offer -> offer.getHasValidPreimage())
                        .collect(Collectors.toList());
                List<Offer> invalidPaidOffers = paidOffers.stream()
                        .filter(offer -> !offer.getHasValidPreimage())
                        .collect(Collectors.toList());

                int paidOffersCount = paidOffers.size();
                int validPaidOffersCount = validPaidOffers.size();
                int invalidPaidOffersCount = invalidPaidOffers.size();

                serverPaidOffersText.setText(Integer.toString(paidOffersCount));
                serverValidPaidOffersText.setText(Integer.toString(validPaidOffersCount));
                serverInvalidPaidOffersText.setText(Integer.toString(invalidPaidOffersCount));
            }
        });

        return root;
    }


}