package io.github.yzernik.squeakand;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;


abstract public class SqueakListAdapter extends RecyclerView.Adapter<SqueakListAdapter.SqueakViewHolder> {

    class SqueakViewHolder extends RecyclerView.ViewHolder {
        public TextView txtSqueakAddress;
        public TextView txtSqueakAuthor;
        public TextView txtSqueakText;
        public TextView txtSqueakBlock;
        public View squeakCardView;
        public View squeakAddressBox;
        public View replyToLine;
        public View replyFromLine;

        public SqueakViewHolder(View view) {
            super(view);

            txtSqueakAddress = view.findViewById(R.id.squeak_item_address);
            txtSqueakAuthor = view.findViewById(R.id.squeak_author);
            txtSqueakText = view.findViewById(R.id.squeak_text);
            txtSqueakBlock = view.findViewById(R.id.squeak_block);
            squeakCardView = view.findViewById(R.id.squeakCardView);
            squeakAddressBox = view.findViewById(R.id.squeak_address_box);
            replyToLine = view.findViewById(R.id.squeak_item_replyto_line);
            replyFromLine = view.findViewById(R.id.squeak_item_replyfrom_line);

            squeakCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(getClass().getName(), "Handling click on squeak.");
                    clickListener.handleItemClick(mSqueaks.get(getAdapterPosition()).squeakEntry.hash);
                }
            });

            txtSqueakAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(getClass().getName(), "Handling click on squeak address.");
                    clickListener.handleItemAddressClick(mSqueaks.get(getAdapterPosition()).squeakEntry.authorAddress);
                }
            });


        }
    }

    private final LayoutInflater mInflater;
    private List<SqueakEntryWithProfile> mSqueaks; // Cached copy of todos
    private ClickListener clickListener;

    public SqueakListAdapter(Context context, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public SqueakViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int cardLayout = getSqueakItemLayout();
        View itemView = mInflater.inflate(cardLayout, parent, false);
        return new SqueakViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SqueakViewHolder holder, int position) {
        if (mSqueaks != null) {
            SqueakEntryWithProfile currentEntry = mSqueaks.get(position);
            holder.txtSqueakAuthor.setText(SqueakDisplayUtil.getAuthorText(currentEntry));
            holder.txtSqueakText.setText(SqueakDisplayUtil.getSqueakText(currentEntry));
            holder.txtSqueakBlock.setText(SqueakDisplayUtil.getBlockTextTimeAgo(currentEntry));
            holder.txtSqueakAddress.setText(SqueakDisplayUtil.getAddressText(currentEntry));

            // Set the visibility of the replyTo line.
            if (currentEntry.squeakEntry.isReply()) {
                holder.replyToLine.setVisibility(View.VISIBLE);
            } else {
                holder.replyToLine.setVisibility(View.INVISIBLE);
            }

            // Set the visibility of the replyFrom line.
            if (itemsHaveReply()) {
                holder.replyFromLine.setVisibility(View.VISIBLE);
            } else {
                holder.replyFromLine.setVisibility(View.INVISIBLE);
            }

        } else {
            // Covers the case of data not being ready yet.
            holder.txtSqueakAuthor.setText("No squeak");
        }
    }

    public void setSqueaks(List<SqueakEntryWithProfile> squeakEntries) {
        mSqueaks = squeakEntries;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mTodos has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mSqueaks != null)
            return mSqueaks.size();
        else return 0;
    }

    public interface ClickListener {
        void handleItemClick(Sha256Hash hash);
        void handleItemAddressClick(String address);
    }

    abstract public int getSqueakItemLayout();

    abstract boolean itemsHaveReply();

}


