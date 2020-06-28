package io.github.yzernik.squeakand.ui.lightningnode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import io.github.yzernik.squeakand.R;
import lnrpc.Rpc;

public class LightningNodeConnectionFragment extends Fragment {

    private TextView mLightningNodePubkeyText;
    private TextView mLightningNodeConnectionStatusText;

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

        mLightningNodePubkeyText = root.findViewById(R.id.lightning_node_pubkey);
        mLightningNodeConnectionStatusText = root.findViewById(R.id.lightning_node_connection_status_text);

        lightningNodeChannelsModel = ViewModelProviders.of(this,
                new LightningNodeConnectionModelFactory(getActivity().getApplication(), pubkey, host))
                .get(LightningNodeConnectionModel.class);

        mLightningNodePubkeyText.setText("Pubkey: " + pubkey);

        lightningNodeChannelsModel.liveIsPeerConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean isPeerConnected) {
                mLightningNodeConnectionStatusText.setText("Is peer connected: " + isPeerConnected);
            }
        });

        return root;
    }

}
