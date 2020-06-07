package io.github.yzernik.squeakand.ui.network;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.github.yzernik.squeakand.R;

public class NetworkFragment extends Fragment {

    private NetworkViewModel networkViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_network, container, false);

        // Get a new or existing ViewModel from the ViewModelProvider.
        networkViewModel = new ViewModelProvider(this).get(NetworkViewModel.class);


        return root;
    }


}
