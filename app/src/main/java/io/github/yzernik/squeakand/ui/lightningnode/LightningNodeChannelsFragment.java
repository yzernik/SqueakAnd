package io.github.yzernik.squeakand.ui.lightningnode;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.ui.money.ChannelListAdapter;
import lnrpc.Rpc;

public class LightningNodeChannelsFragment extends Fragment implements ChannelListAdapter.ClickListener{

    private LightningNodeChannelsModel lightningNodeChannelsModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lightning_node_channels, container, false);

        String pubkey = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            pubkey = this.getArguments().getString("pubkey");
        }

        lightningNodeChannelsModel = ViewModelProviders.of(this,
                new LightningNodeChannelsModelFactory(getActivity().getApplication(), pubkey))
                .get(LightningNodeChannelsModel.class);

        final RecyclerView recyclerView = root.findViewById(R.id.lightning_node_channels_recycler_view);
        final ChannelListAdapter adapter = new ChannelListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        lightningNodeChannelsModel.listNodeChannels().observe(getViewLifecycleOwner(), new Observer<List<Rpc.Channel>>() {
            @Override
            public void onChanged(@Nullable final List<Rpc.Channel> channels) {
                if (channels == null) {
                    return;
                }
                adapter.setProfiles(channels);
            }
        });

        return root;
    }

    private void closeChannel(Rpc.Channel channel) {
        String channelPointString = channel.getChannelPoint();
        LiveData<DataResult<Rpc.CloseStatusUpdate>> closeChannelUpdates = lightningNodeChannelsModel.closeChannel(channelPointString);
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


    @Override
    public void handleItemClick(Rpc.Channel channel) {
        // TODO: go to view channel activity
    }

    @Override
    public void handleItemCloseClick(Rpc.Channel channel) {
        Log.i(getTag(), "Closing channel: " + channel);
        closeChannel(channel);
    }
}
