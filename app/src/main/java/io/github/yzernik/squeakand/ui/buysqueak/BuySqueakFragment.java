package io.github.yzernik.squeakand.ui.buysqueak;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.R;

public class BuySqueakFragment extends Fragment {

    private TextView txtSqueakHash;
    private TextView txtOfferCount;

    private BuySqueakModel buySqueakModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_buy_squeak, container, false);

        Sha256Hash squeakHash = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            String squeakHashStr = this.getArguments().getString("squeak_hash");
            squeakHash = Sha256Hash.wrap(squeakHashStr);
        }

        buySqueakModel = ViewModelProviders.of(this,
                new BuySqueakModelFactory(getActivity().getApplication(), squeakHash))
                .get(BuySqueakModel.class);

        txtSqueakHash = root.findViewById(R.id.buy_squeak_hash);
        txtOfferCount = root.findViewById(R.id.buy_squeak_offers_count_text);

        txtSqueakHash.setText(squeakHash.toString());

        return root;
    }


}
