package io.github.yzernik.squeakand.ui.money;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ChannelViewHolder> {

    class ChannelViewHolder extends RecyclerView.ViewHolder {
        public TextView txtChannelPubkey;
        public TextView txtChannelIsActive;
        public TextView txtChannelCapacity;
        public TextView txtChannelLocalBalance;
        public TextView txtChannelRemoteBalance;
        public Button btnChannelClose;
        public CardView cardView;

        public ChannelViewHolder(View view) {
            super(view);

            txtChannelPubkey = view.findViewById(R.id.channel_item_pubkey_text);
            txtChannelIsActive = view.findViewById(R.id.channel_item_is_active_text);
            txtChannelCapacity = view.findViewById(R.id.channel_item_capacity_text);
            txtChannelLocalBalance = view.findViewById(R.id.channel_item_local_balance_text);
            txtChannelRemoteBalance = view.findViewById(R.id.channel_item_remote_balance_text);
            btnChannelClose = view.findViewById(R.id.channel_item_close_button);
            cardView = view.findViewById(R.id.channelCardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.handleItemClick(mChannels.get(getAdapterPosition()));
                }
            });

            btnChannelClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.handleItemCloseClick(mChannels.get(getAdapterPosition()));
                }
            });
        }
    }

    private final LayoutInflater mInflater;
    private List<Rpc.Channel> mChannels; // Cached copy of channels
    private ChannelListAdapter.ClickListener clickListener;

    public ChannelListAdapter(Context context, ChannelListAdapter.ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public ChannelListAdapter.ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.channel_item_layout, parent, false);
        return new ChannelListAdapter.ChannelViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ChannelListAdapter.ChannelViewHolder holder, int position) {
        if (mChannels != null) {
            Rpc.Channel current = mChannels.get(position);
            holder.txtChannelPubkey.setText("Pubkey: " + current.getRemotePubkey());
            holder.txtChannelIsActive.setText("Is active: " + current.getActive());
            holder.txtChannelCapacity.setText("Capacity: " + current.getCapacity());
            holder.txtChannelLocalBalance.setText("Local balance: " + current.getLocalBalance());
            holder.txtChannelRemoteBalance.setText("Remote balance: " + current.getRemoteBalance());
        } else {
            // Covers the case of data not being ready yet.
            holder.txtChannelPubkey.setText("No pubkey");
        }
    }

    public void setProfiles(List<Rpc.Channel> channels) {
        mChannels = channels;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mTodos has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mChannels != null)
            return mChannels.size();
        else return 0;
    }


    public interface ClickListener {
        void handleItemClick(Rpc.Channel channel);
        void handleItemCloseClick(Rpc.Channel channel);
    }

}
