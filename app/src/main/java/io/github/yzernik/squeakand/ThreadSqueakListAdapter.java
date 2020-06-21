package io.github.yzernik.squeakand;

import android.content.Context;


public class ThreadSqueakListAdapter extends SqueakListAdapter {

    public ThreadSqueakListAdapter(Context context, ClickListener clickListener) {
        super(context, clickListener);
    }

    @Override
    public int getSqueakItemLayout() {
        return R.layout.squeak_thread_item_card_layout;
    }

    @Override
    boolean itemsHaveReply() {
        return true;
    }
}
