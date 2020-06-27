package io.github.yzernik.squeakand.ui.channels;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.ui.money.ChannelListAdapter;
import lnrpc.Rpc;

public class ChannelsFragment extends Fragment implements ChannelListAdapter.ClickListener {

    private ChannelsModel channelsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_channels, container, false);

        channelsViewModel = new ViewModelProvider(this).get(ChannelsModel.class);

        final RecyclerView recyclerView = root.findViewById(R.id.channelsRecyclerView);
        final ChannelListAdapter adapter = new ChannelListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        channelsViewModel.listChannels().observe(getViewLifecycleOwner(), new Observer<Rpc.ListChannelsResponse>() {
            @Override
            public void onChanged(@Nullable final Rpc.ListChannelsResponse listChannelsResponse) {
                if (listChannelsResponse == null) {
                    return;
                }

                for (Rpc.Channel channel: listChannelsResponse.getChannelsList()) {
                    Log.i(getTag(), "Got channel: " + channel);
                }

                // Update the cached copy of the profiles in the adapter.
                List<Rpc.Channel> channels = listChannelsResponse.getChannelsList();
                adapter.setProfiles(channels);
            }
        });

        return root;
    }

    @Override
    public void handleItemClick(Rpc.Channel channel) {
        // TODO: go to view channel activity
    }

    @Override
    public void handleItemCloseClick(Rpc.Channel channel) {
        // TODO: close the channel here
        Log.i(getTag(), "Closing channel: " + channel);
        String channelPointString = channel.getChannelPoint();
        channelsViewModel.closeChannel(channelPointString);
    }

}
