package io.github.yzernik.squeakand.ui.sendpayment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;

public class SendPaymentFragment extends Fragment {

    private TextView txtSendPaymentOffer;

    private SendPaymentModel sendPaymentModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_send_payment, container, false);

        Integer offerId = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            offerId = this.getArguments().getInt("offer_id");
        }

        sendPaymentModel = ViewModelProviders.of(this,
                new SendPaymentModelFactory(getActivity().getApplication(), offerId))
                .get(SendPaymentModel.class);

        txtSendPaymentOffer = root.findViewById(R.id.send_payment_offer);

        txtSendPaymentOffer.setText("Offer id: " + offerId);

        return root;
    }

}