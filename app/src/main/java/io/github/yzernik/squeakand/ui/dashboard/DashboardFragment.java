package io.github.yzernik.squeakand.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.yzernik.squeakand.NewContactActivity;
import io.github.yzernik.squeakand.ProfileListAdapter;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakProfile;

public class DashboardFragment extends Fragment implements ProfileListAdapter.ClickListener {

    public static final int NEW_CONTACT_ACTIVITY_REQUEST_CODE = 1;

    private Button mAddContactButton;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);

        final RecyclerView recyclerView = root.findViewById(R.id.contactsRecyclerView);
        final ProfileListAdapter adapter = new ProfileListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        mAddContactButton = root.findViewById(R.id.add_contact_button);

        dashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);

        mAddContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Add contact button clicked");
                startActivityForResult(new Intent(getActivity(), NewContactActivity.class), NEW_CONTACT_ACTIVITY_REQUEST_CODE);
            }
        });

        dashboardViewModel.getmAllSqueakContactProfiles().observe(getViewLifecycleOwner(), new Observer<List<SqueakProfile>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakProfile> squeakProfiles) {
                // Update the cached copy of the profiles in the adapter.
                adapter.setProfiles(squeakProfiles);
            }
        });

        return root;
    }

    @Override
    public void handleItemClick(int id) {
        // TODO: go to profile activity
        Log.i(getTag(), "Clicked on contact profile id: " + id);
    }

}
