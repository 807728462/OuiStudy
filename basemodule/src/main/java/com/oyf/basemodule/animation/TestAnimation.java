package com.oyf.basemodule.animation;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;
import java.util.List;

public class TestAnimation extends SimpleItemAnimator {
    List<RecyclerView.ViewHolder> mPaddingLists = new ArrayList<>();
    List<RecyclerView.ViewHolder> mAnimationLists = new ArrayList<>();

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        View view = holder.itemView;
        ViewCompat.animate(view).alpha(0);
        mPaddingLists.add(holder);
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        if (!mPaddingLists.isEmpty()) {
            for (int i = 0; i < mPaddingLists.size(); i++) {
                RecyclerView.ViewHolder viewHolder = mPaddingLists.get(i);
                mAnimationLists.add(viewHolder);
                ViewPropertyAnimatorCompat animate = ViewCompat.animate(viewHolder.itemView);
                animate.alpha(1)
                        .setDuration(500)
                        .setListener(new ViewPropertyAnimatorListener() {
                            @Override
                            public void onAnimationStart(View view) {

                            }

                            @Override
                            public void onAnimationEnd(View view) {
                                animate.setListener(null);
                                ViewCompat.animate(viewHolder.itemView).alpha(1);
                                dispatchAddFinished(viewHolder);
                                mAnimationLists.remove(viewHolder);
                                if (!isRunning()) {
                                    dispatchAnimationsFinished();
                                }

                            }

                            @Override
                            public void onAnimationCancel(View view) {

                            }
                        }).start();
            }
            mPaddingLists.clear();
        }
    }

    @Override
    public void endAnimation(@NonNull RecyclerView.ViewHolder item) {
        ViewCompat.animate(item.itemView).cancel();
        for (int i = 0; i < mPaddingLists.size(); i++) {
            RecyclerView.ViewHolder viewHolder = mAnimationLists.get(i);
            if (item == viewHolder) {
                mPaddingLists.remove(viewHolder);
                viewHolder.itemView.setAlpha(1);
                dispatchAddFinished(viewHolder);
            }
        }
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    @Override
    public void endAnimations() {
        for (int i = 0; i < mPaddingLists.size(); i++) {
            RecyclerView.ViewHolder viewHolder = mAnimationLists.get(i);
            mPaddingLists.remove(viewHolder);
            viewHolder.itemView.setAlpha(1);
            dispatchAddFinished(viewHolder);
        }

        for (int i = 0; i < mAnimationLists.size(); i++) {
            RecyclerView.ViewHolder viewHolder = mAnimationLists.get(i);
            ViewCompat.animate(viewHolder.itemView).cancel();
        }
        mAnimationLists.clear();
    }

    @Override
    public boolean isRunning() {
        return !mAnimationLists.isEmpty() || !mPaddingLists.isEmpty();
    }
}
