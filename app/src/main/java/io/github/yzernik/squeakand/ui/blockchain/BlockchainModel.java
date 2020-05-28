package io.github.yzernik.squeakand.ui.blockchain;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.github.yzernik.squeakand.blockchain.ElectrumServerAddress;

public class BlockchainModel extends ViewModel {

    private MutableLiveData<ElectrumServerAddress> mServerAddress;

    public BlockchainModel() {
        mServerAddress = new MutableLiveData<>();
        mServerAddress.setValue(null);
    }

}
