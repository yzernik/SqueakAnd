package io.github.yzernik.squeakand.ui.buysqueak;

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

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.server.SqueakNetworkAsyncClient;

public class BuySqueakFragment extends Fragment {

    private TextView txtSqueakHash;
    private TextView txtOfferCount;

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

        txtSqueakHash.setText(squeakHash.toString());

        buySqueakModel.getOffers().observe(getViewLifecycleOwner(), new Observer<List<Offer>>() {
            @Override
            public void onChanged(@Nullable List<Offer> offers) {
                Log.i(getTag(), "Got offers: " + offers);
                txtOfferCount.setText("Number of offers: " + offers.size());
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
