package io.github.yzernik.squeakand.ui.viewsqueak;

import android.os.Bundle;
import android.util.Log;
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
import io.github.yzernik.squeakand.SqueakEntryWithProfile;

public class ViewSqueakFragment extends Fragment {

    TextView txtSqueakAddress;
    TextView txtSqueakHash;
    TextView txtSqueakText;
    TextView txtSqueakAuthor;

    // private EditText mEditTodoView;
    private ViewSqueakModel todoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_squeak, container, false);

        Sha256Hash squeakHash = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            String squeakHashStr = this.getArguments().getString("squeak_hash");
            squeakHash = Sha256Hash.wrap(squeakHashStr);
        }

        // Get a new or existing ViewModel from the ViewModelProvider.
        todoViewModel = new ViewModelProvider(this).get(ViewSqueakModel.class);

        txtSqueakAddress = root.findViewById(R.id.squeak_item_address);
        txtSqueakHash = root.findViewById(R.id.squeak_author);
        txtSqueakText = root.findViewById(R.id.squeak_text);
        txtSqueakAuthor = root.findViewById(R.id.squeak_block);

        todoViewModel.getSingleTodo(squeakHash).observe(getViewLifecycleOwner(), new Observer<SqueakEntryWithProfile>() {
            @Override
            public void onChanged(@Nullable SqueakEntryWithProfile squeakEntryWithProfile) {
                if (squeakEntryWithProfile == null) {
                    return;
                }
                Log.i(getTag(), "Viewing squeak: " + squeakEntryWithProfile.squeakEntry.getSqueak());
                Log.i(getTag(), "Viewing squeak block: " + squeakEntryWithProfile.squeakEntry.getBlock());

                String authorDisplayString = squeakEntryWithProfile.squeakEntry.authorAddress;
                if (squeakEntryWithProfile.squeakProfile != null) {
                    authorDisplayString = squeakEntryWithProfile.squeakProfile.getName();
                }
                txtSqueakHash.setText(authorDisplayString);
                txtSqueakText.setText(squeakEntryWithProfile.squeakEntry.getDecryptedContentStr());
                txtSqueakAuthor.setText("Block #" + String.valueOf(squeakEntryWithProfile.squeakEntry.blockHeight));
            }
        });

        return root;
    }

}
