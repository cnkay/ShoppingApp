package com.app.shoppingapp.Fragments.SavedRecyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shoppingapp.R;

public class ProductTouchHelperCallback extends ItemTouchHelper.Callback {

    CallbackProductTouch callbackProductTouch;

    public ProductTouchHelperCallback(CallbackProductTouch callbackProductTouch) {
        this.callbackProductTouch = callbackProductTouch;
    }

    // return false for disable
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // Get movement of the recyclerview item
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Change items positions
        callbackProductTouch.itemTouchOnMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        callbackProductTouch.onSwiped(viewHolder, viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        } else {
            final View foregroundView = ((SavedAdapter.SavedViewHolder) viewHolder).viewB;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
        // Will show delete button when swipe
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
            final View foregroundView = ((SavedAdapter.SavedViewHolder) viewHolder).viewF;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
        //Smooth animation when swipe
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //super.clearView(recyclerView, viewHolder);
        final View foregroundView = ((SavedAdapter.SavedViewHolder) viewHolder).viewF;
        /*foregroundView.setBackgroundColor(ContextCompat.getColor(((SavedRecyclerViewAdapter.SavedViewHolder) viewHolder)
                .viewF.getContext(), R.color.colorWhite));*/
        foregroundView.setBackgroundResource(R.drawable.shape4);
        getDefaultUIUtil().clearView(foregroundView);

        // Will clear view when swipe and drag
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(viewHolder != null){
        final View foregroundView = ((SavedAdapter.SavedViewHolder)viewHolder).viewF;
            if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                foregroundView.setBackgroundColor(Color.LTGRAY);
            }

            getDefaultUIUtil().onSelected(foregroundView);
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }


}
