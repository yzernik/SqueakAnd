package io.github.yzernik.squeakand.ui.channels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.github.yzernik.squeakand.lnd.LndRepository;
import lnrpc.Rpc;

public class ChannelsModel extends AndroidViewModel {

    private LndRepository lndRepository;

    public ChannelsModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
    }

    LiveData<Rpc.ListChannelsResponse> listChannels() {
        return lndRepository.listChannels();
    }

    LiveData<Rpc.ClosedChannelUpdate> closeChannel(String channelPoint) {
        return lndRepository.closeChannel(channelPoint);
    }

}
