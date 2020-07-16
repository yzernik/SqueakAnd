package io.github.yzernik.squeakand.ui.buysqueak;

import android.app.Activity;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.server.SqueakNetworkAsyncClient;

public class BuySqueakFragment extends Fragment implements OfferListAdapter.ClickListener {

    int LAUNCH_OFFER_ACTIVITY = 1;

    private TextView txtOfferCount;
    private SwipeRefreshLayout swipeContainer;

    private BuySqueakModel buySqueakModel;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle result from offer activity.
        if (requestCode == LAUNCH_OFFER_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                // Finish the current activity because offer was successfully purchased.
                Log.i(getTag(), "Finishing buy activity because offer purchased.");
                getActivity().finish();
            }
        }
    }

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

        txtOfferCount = root.findViewById(R.id.buy_squeak_offers_count_text);
        swipeContainer = root.findViewById(R.id.buy_squeak_swipe_container);

        final RecyclerView recyclerView = root.findViewById(R.id.buy_squeak_offers_recycler_view);
        final OfferListAdapter adapter = new OfferListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        buySqueakModel.getOffers().observe(getViewLifecycleOwner(), new Observer<List<Offer>>() {
            @Override
            public void onChanged(@Nullable List<Offer> offers) {
                if (offers == null) {
                    return;
                }
                Log.i(getTag(), "Got offers: " + offers);
                // Set the offers list adapter.
                adapter.setOffers(offers);
                txtOfferCount.setText("Number of offers: " + offers.size());
            }
        });

        // Set the swipe action
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                getOffersAsync();
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
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Get offers error: " + e.toString());
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void startOfferActivity(Offer offer) {
        Intent i = new Intent(getActivity(), OfferActivity.class).putExtra("offer_id", offer.getOfferId());
        startActivityForResult(i, LAUNCH_OFFER_ACTIVITY);
    }


    @Override
    public void handleItemClick(Offer offer) {
        // TODO: go to offer activity, and wait for result.
        startOfferActivity(offer);
    }
}
