package io.github.yzernik.squeakand;

import android.content.Context;

public class TimelineSqueakListAdapter extends SqueakListAdapter {

    public TimelineSqueakListAdapter(Context context, ClickListener clickListener) {
        super(context, clickListener);
    }

    @Override
    public int getSqueakItemLayout() {
        return R.layout.squeak_timeline_item_card_layout;
    }

    @Override
    boolean itemsHaveReply() {
        return false;
    }

}
