package io.github.yzernik.squeakand.ui.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.bitcoinj.core.Sha256Hash;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakEntry;

public class ViewTodoFragment extends Fragment {

    TextView txtSqueakBlockNumber;
    TextView txtSqueakHash;
    TextView txtSqueakText;
    TextView txtSqueakAuthor;

    // private EditText mEditTodoView;
    private SqueakViewModel todoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_todo, container, false);

        Sha256Hash squeakHash = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            String squeakHashStr = this.getArguments().getString("squeak_hash");
            squeakHash = Sha256Hash.wrap(squeakHashStr);
        }

        // Get a new or existing ViewModel from the ViewModelProvider.
        todoViewModel = new ViewModelProvider(this).get(SqueakViewModel.class);

        txtSqueakBlockNumber = root.findViewById(R.id.squeak_block_number);
        txtSqueakHash = root.findViewById(R.id.squeak_hash);
        txtSqueakText = root.findViewById(R.id.squeak_text);
        txtSqueakAuthor = root.findViewById(R.id.squeak_author);

        todoViewModel.getSingleTodo(squeakHash).observe(getViewLifecycleOwner(), new Observer<SqueakEntry>() {
            @Override
            public void onChanged(@Nullable SqueakEntry squeakEntry) {
                if (squeakEntry == null) {
                    return;
                }

                txtSqueakHash.setText(squeakEntry.address);
                txtSqueakText.setText(squeakEntry.getDecryptedContentStr());
                txtSqueakAuthor.setText("Block #" + String.valueOf(squeakEntry.blockHeight));
            }
        });

        return root;
    }

}
