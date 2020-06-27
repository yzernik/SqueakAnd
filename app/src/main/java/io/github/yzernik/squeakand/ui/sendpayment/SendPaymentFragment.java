package io.github.yzernik.squeakand.ui.sendpayment;

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

import java.util.Set;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class SendPaymentFragment extends Fragment {

    private TextView txtSendPaymentOffer;
    private TextView txtSendPaymentResult;
    private TextView txtOfferPeerStatus;
    private TextView txtChannelToOfferNode;
    private Button btnOpenChannel;
    private Button btnPay;

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
        txtOfferPeerStatus = root.findViewById(R.id.send_payment_offer_peer_status_text);
        txtChannelToOfferNode = root.findViewById(R.id.channel_to_offer_node_text);
        btnOpenChannel = root.findViewById(R.id.send_payment_open_channel_button);
        btnPay = root.findViewById(R.id.send_payment_pay_button);

        btnPay.setVisibility(View.GONE);

        txtSendPaymentOffer.setText("Offer id: " + offerId);

        // TODO: remove this. Connect peer is used here because channels become inactive when peer is not connected.
        sendPaymentModel.connectPeer();

        // Handle opening channel
        btnOpenChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChannel(20000);
            }
        });
        btnOpenChannel.setVisibility(View.VISIBLE);


        sendPaymentModel.liveOfferChannel().observe(getViewLifecycleOwner(), new Observer<Rpc.Channel>() {
            @Override
            public void onChanged(@Nullable Rpc.Channel channel) {
                if (channel == null) {
                    return;
                }

                Log.i(getTag(), "Got channel to offer node: " + channel);
                txtChannelToOfferNode.setText("Channelpoint" + channel.getChannelPoint() + ", localbalance: "  + channel.getLocalBalance());

                Log.i(getTag(), "channel.getActive(): " + channel.getActive());

                // Send payment when fragment starts
                if (channel.getActive()) {
                    // TODO: check local balance against offer price
                    // if (channel.getLocalBalance() < offerAmount)

                    btnPay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendPayment();
                        }
                    });
                    btnPay.setVisibility(View.VISIBLE);
                }
            }
        });

        /*
            sendPaymentModel.liveConnectedPeers().observe(getViewLifecycleOwner(), new Observer<Set<String>>() {
            @Override
            public void onChanged(@Nullable Set<String> connectedPeers) {
                if (connectedPeers == null) {
                    return;
                }

                Log.i(getTag(), "Got number of connected peers: " + connectedPeers.size());
                txtOfferPeerStatus.setText("Got number of connected peers: " + connectedPeers.size());
                for (String peerPubkey: connectedPeers) {
                    Log.i(getTag(), "Got connected peer pubkey: " + peerPubkey);
                }
            }
        });*/

        sendPaymentModel.liveIsOfferPeerConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isOfferPeerConnected) {
                Log.i(getTag(), "Is offer peer connected: " + isOfferPeerConnected);
                txtOfferPeerStatus.setText("Is offer peer connected: " + isOfferPeerConnected);
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