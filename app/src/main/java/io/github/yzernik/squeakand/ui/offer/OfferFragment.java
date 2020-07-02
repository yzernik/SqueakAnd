package io.github.yzernik.squeakand.ui.offer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.LightningNodeActivity;
import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferWithSqueakServer;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.ViewServerActivity;
import io.github.yzernik.squeakand.ViewServerAddressActivity;
import io.github.yzernik.squeakand.ViewSqueakActivity;
import io.github.yzernik.squeakand.server.SqueakServerAddress;
import lnrpc.Rpc;

public class OfferFragment extends Fragment {

    private TextView txtOfferPrice;
    private Button btnViewSqueak;
    private Button btnViewServer;
    private Button btnViewLightningNode;
    private Button btnPay;


    private OfferModel offerModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_offer, container, false);

        Integer offerId = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            offerId = this.getArguments().getInt("offer_id");
        }

        Log.i(getTag(), "Starting offer fragment with offerId: " + offerId);

        offerModel = ViewModelProviders.of(this,
                new OfferModelFactory(getActivity().getApplication(), offerId))
                .get(OfferModel.class);

        txtOfferPrice = root.findViewById(R.id.offer_price_text);
        btnViewSqueak = root.findViewById(R.id.offer_view_squeak_button);
        btnViewServer = root.findViewById(R.id.offer_view_server_button);
        btnViewLightningNode = root.findViewById(R.id.offer_view_lightning_node_button);
        btnPay = root.findViewById(R.id.offer_pay_button);


        offerModel.getLiveOffer().observe(getViewLifecycleOwner(), new Observer<OfferWithSqueakServer>() {
            @Override
            public void onChanged(@Nullable OfferWithSqueakServer offerWithSqueakServer) {
                if (offerWithSqueakServer == null) {
                    return;
                }

                Offer offer = offerWithSqueakServer.offer;
                SqueakServer squeakServer = offerWithSqueakServer.squeakServer;

                Log.i(getTag(), "Got offer: " + offer);
                txtOfferPrice.setText("Price: " + offer.getAmount() + " satoshis");
                btnViewSqueak.setText(offer.getSqueakHash().toString());
                btnViewServer.setText(squeakServer.serverAddress.toString());
                btnViewLightningNode.setText(offer.getLightningAddress());

                // Set up view squeak button
                btnViewSqueak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startViewSqueakActivity(offer.squeakHash);
                    }
                });

                // Set up view squeak server button
                btnViewServer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startViewSqueakServerActivity(squeakServer.getAddress());
                    }
                });

                // Set up view lightning node button
                btnViewLightningNode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startViewLightningNodeActivity(offer.getPubkey(), offer.getLightningHost());
                    }
                });

                // Set up pay button
                btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendPayment();
                    }
                });

            }
        });


        return root;
    }

    private void sendPayment() {
        offerModel.sendPayment().observe(getViewLifecycleOwner(), new Observer<DataResult<Rpc.SendResponse>>() {
            @Override
            public void onChanged(@Nullable DataResult<Rpc.SendResponse> result) {
                if (result.isFailure()) {
                    handleFailedPayment(result.getError().toString());
                    return;
                }
                handlePaymentResult(result.getResponse());
            }
        });
    }

    private void startViewLightningNodeActivity(String pubkey, String host) {
        startActivity(new Intent(getActivity(), LightningNodeActivity.class)
                .putExtra("pubkey", pubkey)
                .putExtra("host", host)
        );
    }

    private void startViewSqueakActivity(Sha256Hash squeakHash) {
        startActivity(new Intent(getActivity(), ViewSqueakActivity.class)
                .putExtra("squeak_hash", squeakHash.toString())
        );
    }

    private void startViewSqueakServerActivity(SqueakServerAddress serverAddress) {
        startActivity(new Intent(getActivity(), ViewServerAddressActivity.class)
                .putExtra("squeak_server_address", serverAddress.toString())
        );
    }

    private void handlePaymentResult(Rpc.SendResponse response) {
        if (!response.getPaymentPreimage().isEmpty()) {
            handleSuccessfulPayment(response);
        } else {
            handleFailedPayment(response.getPaymentError());
        }
    }

    private void handleSuccessfulPayment(Rpc.SendResponse response) {
        Intent returnIntent = new Intent();
        getActivity().setResult(Activity.RESULT_OK,returnIntent);
        getActivity().finish();
    }

    private void handleFailedPayment(String error) {
        showFailedPaymentAlert(error);
    }


    private void showFailedPaymentAlert(String error) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Payment failed");
        String msg = "Failed with error: " + error;

        if (error.equals("insufficient_balance")) {
            msg += "\nHint: Try opening a new channel to the lightning node to increase your local balance.";
        }

        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}