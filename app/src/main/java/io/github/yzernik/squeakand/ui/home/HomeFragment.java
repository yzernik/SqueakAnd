package io.github.yzernik.squeakand.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.bitcoinj.core.Sha256Hash;

import java.util.List;

import io.github.yzernik.squeakand.CreateSqueakActivity;
import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.ElectrumActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakListAdapter;
import io.github.yzernik.squeakand.TimelineSqueakListAdapter;
import io.github.yzernik.squeakand.ViewAddressActivity;
import io.github.yzernik.squeakand.ViewSqueakActivity;


public class HomeFragment extends Fragment implements SqueakListAdapter.ClickListener {

    private SwipeRefreshLayout swipeContainer;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Enable the options menu
        setHasOptionsMenu(true);

        final RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        final SqueakListAdapter adapter = new TimelineSqueakListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        swipeContainer = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);

        // Get a new or existing ViewModel from the ViewModelProvider.
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedTodos.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        homeViewModel.getmAllSqueaksWithProfile().observe(getViewLifecycleOwner(), new Observer<List<SqueakEntryWithProfile>>() {
            @Override
            public void onChanged(@Nullable final List<SqueakEntryWithProfile> squeakEntriesWithProfile) {
                // Update the cached copy of the squeaks in the adapter.
                adapter.setSqueaks(squeakEntriesWithProfile);
            }
        });

        // Set the swipe action
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.home_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateSqueakActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(getTag(), "Got activity result requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(getTag(), "Called super.onActivityResult...");
    }

    @Override
    public void handleItemClick(Sha256Hash hash) {
        startActivity(new Intent(getActivity(), ViewSqueakActivity.class).putExtra("squeak_hash", hash.toString()));
    }

    @Override
    public void handleItemAddressClick(String address) {
        startActivity(new Intent(getActivity(), ViewAddressActivity.class).putExtra("squeak_address", address));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Set the refresh button action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                fetchTimelineAsync(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        Log.i(getTag(), "Calling syncTimeline...");


        homeViewModel.syncTimeline().observe(getViewLifecycleOwner(), new Observer<DataResult<Integer>>() {
            @Override
            public void onChanged(@Nullable final DataResult<Integer> syncTimelineResult) {
                swipeContainer.setRefreshing(false);

                if (syncTimelineResult.isFailure()) {
                    showSwipeRefreshFailedAlert(syncTimelineResult.getError());
                } else {
                    // TODO: show a snackbar with the number of squeaks downloaded/uploaded.
                }

            }
        });

    }

    private void showSwipeRefreshFailedAlert(Throwable e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sync with squeak servers failed");
        builder.setMessage("Reason: " + e.getMessage());
        // Add the manage electrum button
        builder.setNeutralButton("Manage electrum connection", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Toast.makeText(getContext(), "neutralize", Toast.LENGTH_SHORT).show();
                System.out.println("Manage electrum button clicked");
                startManageElectrum();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void startManageElectrum() {
        Intent intent = new Intent(getActivity(), ElectrumActivity.class);
        startActivity(intent);
    }


}
