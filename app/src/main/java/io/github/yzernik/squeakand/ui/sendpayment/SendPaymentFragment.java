package io.github.yzernik.squeakand.ui.sendpayment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class SendPaymentFragment extends Fragment {

    private TextView txtSendPaymentOffer;
    private TextView txtSendPaymentResult;
    private TextView txtChannelToOfferNode;

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
        txtChannelToOfferNode = root.findViewById(R.id.channel_to_offer_node_text);

        txtSendPaymentOffer.setText("Offer id: " + offerId);

        sendPaymentModel.liveOfferChannel().observe(getViewLifecycleOwner(), new Observer<Rpc.Channel>() {
            @Override
            public void onChanged(@Nullable Rpc.Channel channel) {
                if (channel == null) {
                    return;
                }

                Log.i(getTag(), "Got channel to offer node: " + channel);
                txtChannelToOfferNode.setText("Channelpoint" + channel.getChannelPoint() + ", localbalance: "  + channel.getLocalBalance());
            }
        });

        // Send payment when fragment starts
        sendPayment();

        // TODO: remove
        openChannel(20000);

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

    private void openChannel(long amount) {
        sendPaymentModel.openChannel(amount).observe(getViewLifecycleOwner(), new Observer<Rpc.ChannelPoint>() {
            @Override
            public void onChanged(@Nullable Rpc.ChannelPoint channelPoint) {
                if (channelPoint == null) {
                    return;
                }

                Log.i(getTag(), "Opened channel: " + channelPoint);
            }
        });
    }

}