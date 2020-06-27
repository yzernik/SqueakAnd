package io.github.yzernik.squeakand.ui.channel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import io.github.yzernik.squeakand.lnd.LndRepository;

public class ChannelModel extends AndroidViewModel {

    private long channelId;
    private LndRepository lndRepository;

    public ChannelModel(Application application, long channelId) {
        super(application);
        this.channelId = channelId;
        this.lndRepository = LndRepository.getRepository(application);
    }

}