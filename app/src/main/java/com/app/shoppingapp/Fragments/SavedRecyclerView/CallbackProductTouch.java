package com.app.shoppingapp.Fragments.SavedRecyclerView;

import androidx.recyclerview.widget.RecyclerView;

public interface CallbackProductTouch {
    void itemTouchOnMove(int oldPosition, int newPosition);
    void onSwiped(RecyclerView.ViewHolder viewHolder,int position);
}
