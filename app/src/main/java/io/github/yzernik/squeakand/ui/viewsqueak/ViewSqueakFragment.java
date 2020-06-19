package io.github.yzernik.squeakand.ui.viewsqueak;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.bitcoinj.core.Sha256Hash;

import java.util.Date;

import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.TimeUtil;

public class ViewSqueakFragment extends Fragment {

    private TextView txtSqueakAddress;
    private TextView txtSqueakAuthor;
    private TextView txtSqueakText;
    private TextView txtSqueakBlock;
    private CardView squeakCardView;
    private LinearLayout squeakAddressBox;

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
        txtSqueakAuthor = root.findViewById(R.id.squeak_author);
        txtSqueakText = root.findViewById(R.id.squeak_text);
        txtSqueakBlock = root.findViewById(R.id.squeak_block);
        squeakCardView = root.findViewById(R.id.squeakCardView);
        squeakAddressBox = root.findViewById(R.id.squeak_address_box);

        todoViewModel.getSingleTodo(squeakHash).observe(getViewLifecycleOwner(), new Observer<SqueakEntryWithProfile>() {
            @Override
            public void onChanged(@Nullable SqueakEntryWithProfile squeakEntryWithProfile) {
                if (squeakEntryWithProfile == null) {
                    return;
                }

                /*
                Log.i(getTag(), "Viewing squeak: " + squeakEntryWithProfile.squeakEntry.getSqueak());
                Log.i(getTag(), "Viewing squeak block: " + squeakEntryWithProfile.squeakEntry.getBlock());

                String authorDisplayString = squeakEntryWithProfile.squeakEntry.authorAddress;
                if (squeakEntryWithProfile.squeakProfile != null) {
                    authorDisplayString = squeakEntryWithProfile.squeakProfile.getName();
                }
                txtSqueakHash.setText(authorDisplayString);
                txtSqueakText.setText(squeakEntryWithProfile.squeakEntry.getDecryptedContentStr());
                txtSqueakAuthor.setText("Block #" + String.valueOf(squeakEntryWithProfile.squeakEntry.blockHeight));*/

                txtSqueakAuthor.setText(getAuthorDisplay(squeakEntryWithProfile));
                txtSqueakText.setText(squeakEntryWithProfile.squeakEntry.getDecryptedContentStr());
                txtSqueakBlock.setText(getBlockDisplay(squeakEntryWithProfile));
                txtSqueakAddress.setText(squeakEntryWithProfile.squeakEntry.authorAddress);
            }
        });

        return root;
    }

    private String getBlockDisplay(SqueakEntryWithProfile currentEntry) {
        long blockNumber = currentEntry.squeakEntry.blockHeight;
        Date blockTime = currentEntry.squeakEntry.block.getTime();
        return String.format("Block #%d (%s)", blockNumber, blockTime.toString());
    }

    private String getAuthorDisplay(SqueakEntryWithProfile currentEntry) {
        String authorAddress = currentEntry.squeakEntry.authorAddress;
        SqueakProfile authorProfile = currentEntry.squeakProfile;
        String authorDisplay = authorAddress;
        if (authorProfile != null) {
            String authorName = currentEntry.squeakProfile.getName();
            authorDisplay = authorName;
        }
        return authorDisplay;
    }

}
