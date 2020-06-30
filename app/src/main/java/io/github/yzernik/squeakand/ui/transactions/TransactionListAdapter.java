package io.github.yzernik.squeakand.ui.transactions;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder> {

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTransactionAmount;
        public TextView txtTransactionTime;
        public TextView txtTransactionNumConfirmations;
        public CardView cardView;

        public TransactionViewHolder(View view) {
            super(view);

            txtTransactionAmount = view.findViewById(R.id.transaction_item_amount_text);
            txtTransactionTime = view.findViewById(R.id.transaction_item_time_text);
            txtTransactionNumConfirmations = view.findViewById(R.id.transaction_item_num_confirmations_text);
            cardView = view.findViewById(R.id.transactionCardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.handleItemClick(mTransactions.get(getAdapterPosition()));
                }
            });

        }
    }

    private final LayoutInflater mInflater;
    private List<Rpc.Transaction> mTransactions; // Cached copy of transactions
    private TransactionListAdapter.ClickListener clickListener;

    public TransactionListAdapter(Context context, TransactionListAdapter.ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public TransactionListAdapter.TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.transaction_item_layout, parent, false);
        return new TransactionListAdapter.TransactionViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(TransactionListAdapter.TransactionViewHolder holder, int position) {
        if (mTransactions != null) {
            Rpc.Transaction current = mTransactions.get(position);
            long amount = current.getAmount();
            long timestampSeconds = current.getTimeStamp();
            Date date = new Date(timestampSeconds * 1000);
            String dateString = date.toString();
            holder.txtTransactionAmount.setText(current.getAmount() + " satoshis");
            holder.txtTransactionTime.setText(dateString);
            holder.txtTransactionNumConfirmations.setText(current.getNumConfirmations() + " confirmations");

            // Set the background color
            if (current.getAmount() > 0) {
                holder.cardView.setBackgroundColor(Color.parseColor("#cbffdd"));
            } else if (current.getAmount() < 0) {
                holder.cardView.setBackgroundColor(Color.parseColor("#f3d0d3"));
            } else {
                holder.cardView.setBackgroundColor(Color.parseColor("white"));
            }
        }
    }

    public void setProfiles(List<Rpc.Transaction> transactions) {
        mTransactions = transactions;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mTodos has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mTransactions != null)
            return mTransactions.size();
        else return 0;
    }


    public interface ClickListener {
        void handleItemClick(Rpc.Transaction transaction);
    }

}
