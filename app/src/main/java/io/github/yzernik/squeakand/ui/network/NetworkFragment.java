package io.github.yzernik.squeakand.ui.network;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.github.yzernik.squeakand.ProfileListAdapter;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.ServerListAdapter;

public class NetworkFragment extends Fragment implements ServerListAdapter.ClickListener {

    private NetworkViewModel networkViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_network, container, false);

        final RecyclerView recyclerView = root.findViewById(R.id.serversRecyclerView);
        final ServerListAdapter adapter = new ServerListAdapter(root.getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));


        // Get a new or existing ViewModel from the ViewModelProvider.
        networkViewModel = new ViewModelProvider(this).get(NetworkViewModel.class);


        return root;
    }


    @Override
    public void handleItemClick(int id) {
        // TODO: go to server activity
        Log.i(getTag(), "Clicked on server id: " + id);
        // startActivity(new Intent(getActivity(), ViewServerActivity.class).putExtra("profile_id", id));
    }

}
