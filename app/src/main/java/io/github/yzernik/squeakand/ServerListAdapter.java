package io.github.yzernik.squeakand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerViewHolder> {

    class ServerViewHolder extends RecyclerView.ViewHolder {
        public TextView txtServerName;
        public TextView txtServerAddress;
        public CardView cardView;

        public ServerViewHolder(View view) {
            super(view);

            txtServerName = view.findViewById(R.id.server_item_view_name);
            txtServerAddress = view.findViewById(R.id.server_item_view_address);
            cardView = view.findViewById(R.id.serverCardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.handleItemClick(mServers.get(getAdapterPosition()));
                }
            });

            // TODO: maybe add click listener for delete button inside the card view.
        }
    }

    private final LayoutInflater mInflater;
    private List<SqueakServer> mServers; // Cached copy of servers
    private ClickListener clickListener;

    public ServerListAdapter(Context context, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public ServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.server_item_layout, parent, false);
        return new ServerViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ServerViewHolder holder, int position) {
        if (mServers != null) {
            SqueakServer current = mServers.get(position);
            holder.txtServerName.setText(current.getName());
            holder.txtServerAddress.setText(current.getAddress().toString());
        } else {
            // Covers the case of data not being ready yet.
            holder.txtServerName.setText("No server");
        }
    }

    public void setServers(List<SqueakServer> servers) {
        mServers = servers;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mTodos has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mServers != null)
            return mServers.size();
        else return 0;
    }


    public interface ClickListener {
        void handleItemClick(SqueakServer squeakServer);
    }

}
