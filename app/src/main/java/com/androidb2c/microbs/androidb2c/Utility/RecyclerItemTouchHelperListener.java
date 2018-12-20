package com.androidb2c.microbs.androidb2c.Utility;

import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouchHelperListener {

    void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
