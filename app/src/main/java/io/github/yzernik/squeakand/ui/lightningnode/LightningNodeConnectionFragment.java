package io.github.yzernik.squeakand.ui.lightningnode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;

public class LightningNodeConnectionFragment extends Fragment {

    private LightningNodeConnectionModel lightningNodeChannelsModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lightning_node_connection, container, false);

        String pubkey = null;
        String host = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            pubkey = this.getArguments().getString("pubkey");
            host = this.getArguments().getString("host");
        }

        lightningNodeChannelsModel = ViewModelProviders.of(this,
                new LightningNodeConnectionModelFactory(getActivity().getApplication(), pubkey, host))
                .get(LightningNodeConnectionModel.class);

        return root;
    }

}
