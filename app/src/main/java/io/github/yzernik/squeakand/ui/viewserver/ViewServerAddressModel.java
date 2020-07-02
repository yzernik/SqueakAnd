package io.github.yzernik.squeakand.ui.viewserver;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.yzernik.squeakand.Offer;
import io.github.yzernik.squeakand.OfferRepository;
import io.github.yzernik.squeakand.SqueakServer;
import io.github.yzernik.squeakand.SqueakServerRepository;
import io.github.yzernik.squeakand.server.SqueakServerAddress;

public class ViewServerAddressModel extends AndroidViewModel {

    private SqueakServerAddress serverAddress;

    private SqueakServerRepository mRepository;
    private OfferRepository offerRepository;

    private LiveData<SqueakServer> liveSqueakServer;
    private LiveData<List<Offer>> livePaidOffers;

    public ViewServerAddressModel(@NonNull Application application, SqueakServerAddress serverAddress) {
        super(application);
        this.serverAddress = serverAddress;
        mRepository = SqueakServerRepository.getRepository(application);
        offerRepository = OfferRepository.getRepository(application);

        liveSqueakServer = mRepository.getSqueakServerByAddress(serverAddress);
        livePaidOffers = offerRepository.getPaidOffersForServer(serverAddress);
    }

    LiveData<SqueakServer> getLiveSqueakServer() {
        return liveSqueakServer;
    }

    LiveData<List<Offer>> getLivePaidOffers() {
        return livePaidOffers;
    }

}
