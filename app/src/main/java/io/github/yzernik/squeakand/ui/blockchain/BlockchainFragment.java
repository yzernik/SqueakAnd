package io.github.yzernik.squeakand.ui.blockchain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;

public class BlockchainFragment extends Fragment {

    private Spinner mProfilesSpinner;
    private Button mCreateProfileButton;

    private BlockchainModel blockchainModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blockchain, container, false);

        blockchainModel = new ViewModelProvider(getActivity()).get(BlockchainModel.class);

        return root;
    }

}
