package io.github.yzernik.squeakand.ui.channels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class PendingOpenChannelListAdapter extends RecyclerView.Adapter<PendingOpenChannelListAdapter.PendingOpenChannelViewHolder> {

    class PendingOpenChannelViewHolder extends RecyclerView.ViewHolder {
        public TextView txtChannelPubkey;
        public TextView txtConfirmationHeight;
        public TextView txtChannelCapacity;
        public TextView txtChannelLocalBalance;
        public TextView txtChannelRemoteBalance;
        public CardView cardView;

        public PendingOpenChannelViewHolder(View view) {
            super(view);

            txtChannelPubkey = view.findViewById(R.id.pending_open_channel_item_pubkey_text);
            txtConfirmationHeight = view.findViewById(R.id.pending_open_channel_confirmation_height);
            txtChannelCapacity = view.findViewById(R.id.pending_open_channel_item_capacity_text);
            txtChannelLocalBalance = view.findViewById(R.id.pending_open_channel_item_local_balance_text);
            txtChannelRemoteBalance = view.findViewById(R.id.pending_open_channel_item_remote_balance_text);
            cardView = view.findViewById(R.id.pending_open_channel_card_view);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.handleItemClick(mPendingOpenChannels.get(getAdapterPosition()));
                }
            });
        }
    }

    private final LayoutInflater mInflater;
    private List<Rpc.PendingChannelsResponse.PendingOpenChannel> mPendingOpenChannels; // Cached copy of channels
    private PendingOpenChannelListAdapter.ClickListener clickListener;

    public PendingOpenChannelListAdapter(Context context, PendingOpenChannelListAdapter.ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public PendingOpenChannelListAdapter.PendingOpenChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.pending_open_channel_item_layout, parent, false);
        return new PendingOpenChannelListAdapter.PendingOpenChannelViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(PendingOpenChannelListAdapter.PendingOpenChannelViewHolder holder, int position) {
        if (mPendingOpenChannels != null) {
            Rpc.PendingChannelsResponse.PendingOpenChannel current = mPendingOpenChannels.get(position);
            holder.txtChannelPubkey.setText("Pubkey: " + current.getChannel().getRemoteNodePub());
            holder.txtConfirmationHeight.setText("Confirmation Height: " + current.getConfirmationHeight());
            holder.txtChannelCapacity.setText("Capacity: " + current.getChannel().getCapacity());
            holder.txtChannelLocalBalance.setText("Local balance: " + current.getChannel().getLocalBalance());
            holder.txtChannelRemoteBalance.setText("Remote balance: " + current.getChannel().getRemoteBalance());
        } else {
            // Covers the case of data not being ready yet.
            holder.txtChannelPubkey.setText("No pubkey");
        }
    }

    public void setPendingOpenChannels(List<Rpc.PendingChannelsResponse.PendingOpenChannel> pendingOpenChannels) {
        mPendingOpenChannels = pendingOpenChannels;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mTodos has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPendingOpenChannels != null)
            return mPendingOpenChannels.size();
        else return 0;
    }


    public interface ClickListener {
        void handleItemClick(Rpc.PendingChannelsResponse.PendingOpenChannel pendingOpenChannel);
    }

}
