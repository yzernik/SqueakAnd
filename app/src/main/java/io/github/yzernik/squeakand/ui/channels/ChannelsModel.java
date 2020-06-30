package io.github.yzernik.squeakand.ui.channels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.DataResult;
import io.github.yzernik.squeakand.lnd.LndRepository;
import lnrpc.Rpc;

public class ChannelsModel extends AndroidViewModel {

    private LndRepository lndRepository;

    public ChannelsModel(@NonNull Application application) {
        super(application);
        this.lndRepository = LndRepository.getRepository(application);
    }

    LiveData<List<Rpc.Channel>> listChannels() {
        // return lndRepository.listChannels();

        return lndRepository.getLiveChannels();
    }

    LiveData<DataResult<Rpc.CloseStatusUpdate>> closeChannel(String channelPoint) {
        return lndRepository.closeChannel(channelPoint, false);
    }

}
