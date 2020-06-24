package io.github.yzernik.squeakand.ui.buysqueak;

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

import org.bitcoinj.core.Sha256Hash;

import java.util.List;
import java.util.Locale;

import io.github.yzernik.squeakand.NewProfileActivity;
import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.server.SqueakNetworkAsyncClient;

public class BuySqueakFragment extends Fragment {

    private TextView txtSqueakHash;
    private TextView txtOfferCount;
    private Button btnPayBestOffer;

    private BuySqueakModel buySqueakModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_buy_squeak, container, false);

        Sha256Hash squeakHash = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            String squeakHashStr = this.getArguments().getString("squeak_hash");
            squeakHash = Sha256Hash.wrap(squeakHashStr);
        }

        buySqueakModel = ViewModelProviders.of(this,
                new BuySqueakModelFactory(getActivity().getApplication(), squeakHash))
                .get(BuySqueakModel.class);

        txtSqueakHash = root.findViewById(R.id.buy_squeak_hash);
        txtOfferCount = root.findViewById(R.id.buy_squeak_offers_count_text);
        btnPayBestOffer = root.findViewById(R.id.buy_squeak_buy_button);

        txtSqueakHash.setText(squeakHash.toString());

        buySqueakModel.getOffers().observe(getViewLifecycleOwner(), new Observer<List<Offer>>() {
            @Override
            public void onChanged(@Nullable List<Offer> offers) {
                Log.i(getTag(), "Got offers: " + offers);
                txtOfferCount.setText("Number of offers: " + offers.size());
            }
        });

        buySqueakModel.getBestOffer().observe(getViewLifecycleOwner(), new Observer<Offer>() {
            @Override
            public void onChanged(@Nullable Offer offer) {
                if(offer == null) {
                    return;
                }

                Log.i(getTag(), "Got best offer: " + offer);
                String buyBtnText = String.format(Locale.ENGLISH, "Pay %d satoshis to buy the squeak.", offer.amount);
                btnPayBestOffer.setText(buyBtnText);
                btnPayBestOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Buy button clicked");
                    }
                });
            }
        });

        // Start loading offers when the fragment starts.
        getOffersAsync();

        return root;
    }

    public void getOffersAsync() {
        Log.i(getTag(), "Calling getOffersAsync...");

        SqueakNetworkAsyncClient asyncClient = buySqueakModel.getAsyncClient();
        asyncClient.getOffers(buySqueakModel.getSqueakHash(), new SqueakNetworkAsyncClient.SqueakServerResponseHandler() {
            @Override
            public void onSuccess() {
                Log.i(getTag(), "Finished getting offers with success.");

                // TODO: show the progress has finished
                // swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Get offers error: " + e.toString());
            }
        });

    }


}
