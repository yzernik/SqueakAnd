package io.github.yzernik.squeakand.ui.buysqueak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.R;

public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.OfferViewHolder> {

    class OfferViewHolder extends RecyclerView.ViewHolder {
        public TextView txtOfferPrice;
        public TextView txtOfferSqueakHash;
        public TextView txtOfferServerAddress;
        public CardView cardView;

        public OfferViewHolder(View view) {
            super(view);

            txtOfferPrice = view.findViewById(R.id.offer_item_view_price_text);
            txtOfferSqueakHash = view.findViewById(R.id.offer_item_view_squeak_hash_text);
            txtOfferServerAddress = view.findViewById(R.id.offer_item_view_server_address_text);
            cardView = view.findViewById(R.id.offerCardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.handleItemClick(mOffers.get(getAdapterPosition()));
                }
            });
        }
    }

    private final LayoutInflater mInflater;
    private List<Offer> mOffers; // Cached copy of offers
    private OfferListAdapter.ClickListener clickListener;

    public OfferListAdapter(Context context, OfferListAdapter.ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public OfferListAdapter.OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.offer_item_layout, parent, false);
        return new OfferListAdapter.OfferViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(OfferListAdapter.OfferViewHolder holder, int position) {
        if (mOffers != null) {
            Offer current = mOffers.get(position);
            holder.txtOfferPrice.setText("Price: " + current.getAmount() + " satoshis");
            holder.txtOfferSqueakHash.setText("Squeak hash: " + current.getSqueakHash());
            holder.txtOfferServerAddress.setText("Server host: " + current.getSqueakServerAddress());
        }
    }

    public void setOffers(List<Offer> offers) {
        mOffers = offers;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mTodos has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mOffers != null)
            return mOffers.size();
        else return 0;
    }


    public interface ClickListener {
        void handleItemClick(Offer offer);
    }

}
