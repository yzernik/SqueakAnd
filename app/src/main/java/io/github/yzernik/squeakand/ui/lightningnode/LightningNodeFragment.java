package io.github.yzernik.squeakand.ui.lightningnode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.yzernik.squeakand.R;

public class LightningNodeFragment extends Fragment {

    private TextView txtLightningNodePubkey;
    private TextView txtLightningNodeHost;

    private LightningNodeModel lightningNodeModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lightning_node, container, false);

        String pubkey = null;
        String host = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            pubkey = this.getArguments().getString("pubkey");
            host = this.getArguments().getString("host");
        }

        lightningNodeModel = ViewModelProviders.of(this,
                new LightningNodeModelFactory(getActivity().getApplication(), pubkey, host))
                .get(LightningNodeModel.class);

        txtLightningNodePubkey = root.findViewById(R.id.lightning_node_pubkey);
        txtLightningNodeHost = root.findViewById(R.id.lightning_node_host);

        txtLightningNodePubkey.setText(pubkey);
        txtLightningNodeHost.setText(host);

        return root;
    }


}
