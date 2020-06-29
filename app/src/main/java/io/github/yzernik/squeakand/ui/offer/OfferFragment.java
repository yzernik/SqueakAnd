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

import io.github.yzernik.squeakand.LightningNodeActivity;
import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class OfferFragment extends Fragment {

    private TextView txtOfferPrice;
    private TextView txtOfferSqueakHash;
    private TextView txtOfferServerAddress;
    private TextView txtOfferLightningAddress;
    private Button btnPay;
    private Button btnViewLightningNode;


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
        txtOfferSqueakHash = root.findViewById(R.id.offer_squeak_hash_text);
        txtOfferServerAddress = root.findViewById(R.id.offer_server_address_text);
        txtOfferLightningAddress = root.findViewById(R.id.offer_lightning_address_text);
        btnPay = root.findViewById(R.id.offer_pay_button);
        btnViewLightningNode = root.findViewById(R.id.offer_view_lightning_node);

        btnPay.setVisibility(View.GONE);
        btnViewLightningNode.setVisibility(View.GONE);

        offerModel.getLiveOffer().observe(getViewLifecycleOwner(), new Observer<Offer>() {
            @Override
            public void onChanged(@Nullable Offer offer) {
                if (offer == null) {
                    return;
                }

                Log.i(getTag(), "Got offer: " + offer);
                txtOfferPrice.setText("Price: " + offer.getAmount() + " satoshis");
                txtOfferSqueakHash.setText("Squeak hash: " + offer.getSqueakHash());
                txtOfferServerAddress.setText("Server: " + offer.squeakServerId);
                txtOfferLightningAddress.setText("Lightning address: " + offer.getLightningAddress());

                // Set up view lightning node button
                btnViewLightningNode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startViewLightningNodeActivity(offer.pubkey, offer.host);
                    }
                });
                btnViewLightningNode.setVisibility(View.VISIBLE);


                // Set up pay button
                btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendPayment();
                    }
                });
                btnPay.setVisibility(View.VISIBLE);

            }
        });


        return root;
    }

    private void sendPayment() {
        offerModel.sendPayment().observe(getViewLifecycleOwner(), new Observer<Rpc.SendResponse>() {
            @Override
            public void onChanged(@Nullable Rpc.SendResponse response) {
                if (response == null) {
                    return;
                }
                handlePaymentResult(response);
            }
        });
    }

    private void startViewLightningNodeActivity(String pubkey, String host) {
        Log.i(getTag(), "Going to view lightning node activity...");

        startActivity(new Intent(getActivity(), LightningNodeActivity.class)
                .putExtra("pubkey", pubkey)
                .putExtra("host", host)
        );
    }

    private void handlePaymentResult(Rpc.SendResponse response) {
        if (!response.getPaymentPreimage().isEmpty()) {
            handleSuccessfulPayment(response);
        } else {
            handleFailedPayment(response);
        }
    }

    private void handleSuccessfulPayment(Rpc.SendResponse response) {
        Intent returnIntent = new Intent();
        getActivity().setResult(Activity.RESULT_OK,returnIntent);
        getActivity().finish();
    }

    private void handleFailedPayment(Rpc.SendResponse response) {
        String error = response.getPaymentError();
        showFailedPaymentAlert(error);
    }


    private void showFailedPaymentAlert(String error) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Payment failed");
        String msg = "Failed with error: " + error;

        if (error.equals("insufficient_balance")) {
            msg += "\nHint: Try opening a new channel to increase your local balance.";
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