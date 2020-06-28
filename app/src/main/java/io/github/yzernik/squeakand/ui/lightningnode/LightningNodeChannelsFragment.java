package io.github.yzernik.squeakand.ui.lightningnode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        LiveData<Rpc.ClosedChannelUpdate> closeChannelUpdates = lightningNodeChannelsModel.closeChannel(channelPointString);
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
