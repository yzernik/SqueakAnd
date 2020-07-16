package io.github.yzernik.squeakand.ui.channels;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class ChannelsFragment extends Fragment implements ChannelListAdapter.ClickListener, PendingOpenChannelListAdapter.ClickListener {

    private ChannelsModel channelsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_channels, container, false);

        channelsViewModel = new ViewModelProvider(this).get(ChannelsModel.class);

        // Set up the pending open channels recycler view.
        final RecyclerView pendingOpenChannelsRecyclerView = root.findViewById(R.id.pending_open_channels_recycler_view);
        final PendingOpenChannelListAdapter pendingOpenChannelListAdapter = new PendingOpenChannelListAdapter(root.getContext(), this);
        pendingOpenChannelsRecyclerView.setAdapter(pendingOpenChannelListAdapter);
        pendingOpenChannelsRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        // Set up the channels recycler view.
        final RecyclerView recyclerView = root.findViewById(R.id.channelsRecyclerView);
        final ChannelListAdapter adapter = new ChannelListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        channelsViewModel.pendingChannels().observe(getViewLifecycleOwner(), new Observer<Rpc.PendingChannelsResponse>() {
            @Override
            public void onChanged(@Nullable final Rpc.PendingChannelsResponse pendingChannelsResponse) {
                List<Rpc.PendingChannelsResponse.PendingOpenChannel> pendingOpenChannels = pendingChannelsResponse.getPendingOpenChannelsList();
                // Update the cached copy of the channels in the adapter.
                pendingOpenChannelListAdapter.setPendingOpenChannels(pendingOpenChannels);
            }
        });

        channelsViewModel.listChannels().observe(getViewLifecycleOwner(), new Observer<Rpc.ListChannelsResponse>() {
            @Override
            public void onChanged(@Nullable final Rpc.ListChannelsResponse response) {
                List<Rpc.Channel> channels = response.getChannelsList();
                // Update the cached copy of the channels in the adapter.
                adapter.setProfiles(channels);
            }
        });

        return root;
    }

    private void closeChannel(Rpc.Channel channel) {
        String channelPointString = channel.getChannelPoint();
        LiveData<DataResult<Rpc.CloseStatusUpdate>> closeChannelUpdates = channelsViewModel.closeChannel(channelPointString);
        handleCloseChannelResult(closeChannelUpdates);
    }

    private void handleCloseChannelResult(LiveData<DataResult<Rpc.CloseStatusUpdate>> closeChannelUpdate) {
        closeChannelUpdate.observe(getViewLifecycleOwner(), new Observer<DataResult<Rpc.CloseStatusUpdate>>() {
            @Override
            public void onChanged(@Nullable final DataResult<Rpc.CloseStatusUpdate> updateResult) {
                if (updateResult.isFailure()) {
                    Log.e(getTag(), "Close channel failed with error: " + updateResult.getError());
                    showFailedCloseChannelAlert(updateResult.getError());
                    return;
                }
                Log.e(getTag(), "Close channel update: " + updateResult.getResponse());
                showSuccessCloseChannelAlert();
            }
        });
    }

    private void showFailedCloseChannelAlert(Throwable e) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Close channel failed");
        alertDialog.setMessage("Failed with error: " + e);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showSuccessCloseChannelAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Closed channel");
        alertDialog.setMessage("Channel close is now pending.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showConfirmCloseChannelAlert(Rpc.Channel channel) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Close channel");
        alertDialog.setMessage("Closing the channel will result in a bitcoin transaction fee." +
                " Are you sure you want to close the channel?");
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(getTag(), "Closing channel...");
                        closeChannel(channel);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void handleItemClick(Rpc.Channel channel) {
        // TODO: go to view channel activity
    }

    @Override
    public void handleItemCloseClick(Rpc.Channel channel) {
        showConfirmCloseChannelAlert(channel);
    }

    @Override
    public void handleItemClick(Rpc.PendingChannelsResponse.PendingOpenChannel pendingOpenChannel) {
        // TODO: go to view channel activity
    }

}
