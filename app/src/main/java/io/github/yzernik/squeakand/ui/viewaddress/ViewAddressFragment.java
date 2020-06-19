package io.github.yzernik.squeakand.ui.viewaddress;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;

public class ViewAddressFragment extends Fragment {

    private ViewAddressModel viewAddressModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_address, container, false);

        String squeakAddress = this.getArguments().getString("squeak_address");

        Log.i(getTag(), "Starting ViewAddressFragment with squeak address: " + squeakAddress);

        viewAddressModel = new ViewModelProvider(getActivity()).get(ViewAddressModel.class);

        return root;
    }

}