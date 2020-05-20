package io.github.yzernik.squeakand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;


public class SqueakListAdapter extends RecyclerView.Adapter<SqueakListAdapter.SqueakViewHolder> {

    class SqueakViewHolder extends RecyclerView.ViewHolder {
        public TextView txtSqueakBlockNumber;
        public TextView txtSqueakHash;
        public TextView txtSqueakText;
        public TextView txtSqueakAuthor;
        public CardView squeakCardView;

        public SqueakViewHolder(View view) {
            super(view);

            txtSqueakBlockNumber = view.findViewById(R.id.squeak_block_number);
            txtSqueakHash = view.findViewById(R.id.squeak_hash);
            txtSqueakText = view.findViewById(R.id.squeak_text);
            txtSqueakAuthor = view.findViewById(R.id.squeak_author);
            squeakCardView = view.findViewById(R.id.squeakCardView);

            squeakCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.handleItemClick(mSqueaks.get(getAdapterPosition()).squeakEntry.hash);
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
        View itemView = mInflater.inflate(R.layout.squeak_item_layout, parent, false);
        return new SqueakViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SqueakViewHolder holder, int position) {
        if (mSqueaks != null) {
            SqueakEntryWithProfile currentEntry = mSqueaks.get(position);
            String authorAddress = currentEntry.squeakEntry.authorAddress;
            String authorName = currentEntry.squeakProfile.getName();
            String authorDisplay = authorAddress;
            if (authorName != null) {
                authorDisplay = authorName + " (" + authorAddress + ")";
            }
            // holder.txtSqueakHash.setText(currentEntry.squeakEntry.authorAddress);
            holder.txtSqueakHash.setText(authorDisplay);
            holder.txtSqueakText.setText(currentEntry.squeakEntry.getDecryptedContentStr());
            holder.txtSqueakAuthor.setText("Block #" + String.valueOf(currentEntry.squeakEntry.blockHeight));
        } else {
            // Covers the case of data not being ready yet.
            holder.txtSqueakHash.setText("No squeak");
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
    }
}


