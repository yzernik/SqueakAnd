package io.github.yzernik.squeakand.ui.viewsqueak;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.List;

import io.github.yzernik.squeakand.CreateSqueakActivity;
import io.github.yzernik.squeakand.R;
import io.github.yzernik.squeakand.SqueakDisplayUtil;
import io.github.yzernik.squeakand.SqueakEntry;
import io.github.yzernik.squeakand.SqueakEntryWithProfile;
import io.github.yzernik.squeakand.SqueakProfile;
import io.github.yzernik.squeakand.TimeUtil;
import io.github.yzernik.squeakand.ViewAddressActivity;
import io.github.yzernik.squeakand.ViewProfileActivity;

public class ViewSqueakFragment extends Fragment {

    private TextView txtSqueakAddress;
    private TextView txtSqueakAuthor;
    private TextView txtSqueakText;
    private TextView txtSqueakBlock;
    private View squeakActionBox;
    private ImageButton replyImageButton;
    private CardView squeakCardView;
    private View squeakAddressBox;

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
        squeakActionBox = root.findViewById(R.id.squeak_action_box);
        replyImageButton = root.findViewById(R.id.reply_image_button);

        // Make the action bar visible.
        squeakActionBox.setVisibility(View.VISIBLE);

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

                txtSqueakAuthor.setText(SqueakDisplayUtil.getAuthorText(squeakEntryWithProfile));
                txtSqueakText.setText(SqueakDisplayUtil.getSqueakText(squeakEntryWithProfile));
                txtSqueakBlock.setText(SqueakDisplayUtil.getBlockText(squeakEntryWithProfile));
                txtSqueakAddress.setText(SqueakDisplayUtil.getAddressText(squeakEntryWithProfile));

                // Go to the view address activity on author address click.
                squeakAddressBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ViewAddressActivity.class).putExtra("squeak_address", squeakEntryWithProfile.squeakEntry.authorAddress));
                    }
                });

                // Go to the create squeak activity on reply button click.
                replyImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), CreateSqueakActivity.class).putExtra("reply_to_hash", squeakEntryWithProfile.squeakEntry.hash.toString()));
                    }
                });
            }
        });

        todoViewModel.getThreadAncestorSqueaks(squeakHash).observe(getViewLifecycleOwner(), new Observer<List<SqueakEntry>>() {
            @Override
            public void onChanged(@Nullable List<SqueakEntry> threadAncestorSqueaks) {
                // TODO: Handle thread ancestor squeaks.
                Log.i(getTag(), "Got result from thread ancestor query: " + threadAncestorSqueaks);
                Log.i(getTag(), "Got result from thread ancestor query (size): " + threadAncestorSqueaks.size());
                for (SqueakEntry squeakEntry: threadAncestorSqueaks) {
                    Log.i(getTag(), "Ancestor squeak: " + squeakEntry.getSqueak());
                }
            }
        });

        return root;
    }

}
