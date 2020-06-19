package io.github.yzernik.squeakand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder> {

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        public TextView txtProfileName;
        public TextView txtProfileAddress;
        public CardView cardView;

        public ProfileViewHolder(View view) {
            super(view);

            txtProfileName = view.findViewById(R.id.profile_item_view_name);
            txtProfileAddress = view.findViewById(R.id.profile_item_view_address);
            cardView = view.findViewById(R.id.profileCardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.handleItemClick(mProfiles.get(getAdapterPosition()));
                }
            });
        }
    }

    private final LayoutInflater mInflater;
    private List<SqueakProfile> mProfiles; // Cached copy of profiles
    private ClickListener clickListener;

    public ProfileListAdapter(Context context, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.profile_item_layout, parent, false);
        return new ProfileViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        if (mProfiles != null) {
            SqueakProfile current = mProfiles.get(position);
            holder.txtProfileName.setText(current.getName());
            holder.txtProfileAddress.setText(current.getAddress());
        } else {
            // Covers the case of data not being ready yet.
            holder.txtProfileName.setText("No profile");
        }
    }

    public void setProfiles(List<SqueakProfile> profiles) {
        mProfiles = profiles;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mTodos has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mProfiles != null)
            return mProfiles.size();
        else return 0;
    }


    public interface ClickListener {
        void handleItemClick(SqueakProfile profile);
    }

}
