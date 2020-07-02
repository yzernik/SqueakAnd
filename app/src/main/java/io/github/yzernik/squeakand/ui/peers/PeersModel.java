package io.github.yzernik.squeakand.ui.peers;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import io.github.yzernik.squeakand.lnd.LndRepository;
import io.github.yzernik.squeakand.DataResult;
import lnrpc.Rpc;

public class PeersModel extends AndroidViewModel {

    private LndRepository lndRepository;
    private LiveData<Rpc.ListPeersResponse> liveListPeersResponse;

    public PeersModel(Application application) {
        super(application);
        lndRepository = LndRepository.getRepository(application);
        liveListPeersResponse = lndRepository.listPeers();
    }

    public LiveData<Rpc.ListPeersResponse> getLiveListPeersResponse() {
        return liveListPeersResponse;
    }

    public LiveData<List<Rpc.Peer>> getLivePeers() {
        return Transformations.map(liveListPeersResponse, response -> {
            return response.getPeersList();
        });
    }


}