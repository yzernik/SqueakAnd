package io.github.yzernik.squeakand.ui.sendpayment;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.LightningNodeActivity;
import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class SendPaymentFragment extends Fragment {

    private TextView txtSendPaymentOffer;
    private TextView txtSendPaymentResult;
    private Button btnPay;
    private Button btnViewLightningNode;


    private SendPaymentModel sendPaymentModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_send_payment, container, false);

        Integer offerId = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            offerId = this.getArguments().getInt("offer_id");
        }

        Log.i(getTag(), "Starting sendpayment fragment with offerId: " + offerId);

        sendPaymentModel = ViewModelProviders.of(this,
                new SendPaymentModelFactory(getActivity().getApplication(), offerId))
                .get(SendPaymentModel.class);

        txtSendPaymentOffer = root.findViewById(R.id.send_payment_offer);
        txtSendPaymentResult = root.findViewById(R.id.send_payment_payment_result_text);
        btnPay = root.findViewById(R.id.send_payment_pay_button);
        btnViewLightningNode = root.findViewById(R.id.send_payment_view_lightning_node);


        btnPay.setVisibility(View.GONE);

        txtSendPaymentOffer.setText("Offer id: " + offerId);

        // Set up pay button
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPayment();
            }
        });
        btnPay.setVisibility(View.VISIBLE);


        sendPaymentModel.getLiveOffer().observe(getViewLifecycleOwner(), new Observer<Offer>() {
            @Override
            public void onChanged(@Nullable Offer offer) {
                Log.i(getTag(), "Got offer: " + offer);

                btnViewLightningNode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startViewLightningNodeActivity(offer.pubkey, offer.host);
                    }
                });

            }
        });


        return root;
    }

    private void sendPayment() {
        sendPaymentModel.sendPayment().observe(getViewLifecycleOwner(), new Observer<Rpc.SendResponse>() {
            @Override
            public void onChanged(@Nullable Rpc.SendResponse response) {
                if (response == null) {
                    return;
                }

                // Finish the activity if payment succeeded
                if (!response.getPaymentPreimage().isEmpty()) {
                    getActivity().finish();
                }

                Log.i(getTag(), "Got payment response: " + response);
                txtSendPaymentResult.setText("Payment response error: " + response.getPaymentError());
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

}