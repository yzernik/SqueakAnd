package io.github.yzernik.squeakand.ui.viewaddress;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakListAdapter;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.ViewProfileActivity;
import io.github.yzernik.squeakand.ViewSqueakActivity;

public class ViewAddressFragment extends Fragment implements SqueakListAdapter.ClickListener {

    private TextView addressTextView;
    private TextView profileNameTextView;
    private Button editProfileButton;
    private FrameLayout missingProfileBanner;
    private FrameLayout presentProfileBanner;

    private ViewAddressModel viewAddressModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_address, container, false);

        String squeakAddress = this.getArguments().getString("squeak_address");

        Log.i(getTag(), "Starting ViewAddressFragment with squeak address: " + squeakAddress);

        final RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        final SqueakListAdapter adapter = new SqueakListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        addressTextView = root.findViewById(R.id.address_string_text);
        missingProfileBanner = root.findViewById(R.id.address_profile_missing_layout);
        presentProfileBanner = root.findViewById(R.id.address_profile_present_layout);
        profileNameTextView = root.findViewById(R.id.address_profile_name_text);
        editProfileButton = root.findViewById(R.id.address_edit_profile_button);

        viewAddressModel = new ViewModelProvider(getActivity()).get(ViewAddressModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedTodos.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        viewAddressModel.getmAllAddressSqueaksWithProfile(squeakAddress).observe(getViewLifecycleOwner(), new Observer<List<SqueakEntryWithProfile>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakEntryWithProfile> squeakEntriesWithProfile) {
                // Update the cached copy of the squeaks in the adapter.
                adapter.setSqueaks(squeakEntriesWithProfile);
            }
        });

        addressTextView.setText(squeakAddress);

        viewAddressModel.getSqueakProfileByAddress(squeakAddress).observe(getViewLifecycleOwner(), new Observer<SqueakProfile>() {
            @Override
            public void onChanged(@Nullable final SqueakProfile profile) {
                if (profile == null) {
                    missingProfileBanner.setVisibility(View.VISIBLE);
                } else {
                    presentProfileBanner.setVisibility(View.VISIBLE);
                    profileNameTextView.setText(profile.getName());
                    editProfileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), ViewProfileActivity.class).putExtra("profile_id", profile.getProfileId()));
                        }
                    });
                }

            }
        });

        return root;
    }

    @Override
    public void handleItemClick(Sha256Hash hash) {
        startActivity(new Intent(getActivity(), ViewSqueakActivity.class).putExtra("squeak_hash", hash.toString()));
    }

    @Override
    public void handleItemAddressClick(String address) {
        // Do nothing because it is the same address as the current activity.
    }

}