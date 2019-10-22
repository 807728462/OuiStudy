package com.oyf.basemodule.animation;

import android.animation.Animator;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;
import java.util.List;

public class MyAnimation extends SimpleItemAnimator {
    private List<RecyclerView.ViewHolder> mAddHolder = new ArrayList<>();//需要的viewholder
    private List<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();

    private List<RecyclerView.ViewHolder> mRemoveHolder = new ArrayList<>();//需要的viewholder
    private List<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();

    //private List<RecyclerView.ViewHolder> mAddAnimations=new ArrayList<>();
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        holder.itemView.setAlpha(1);
        mRemoveHolder.add(holder);
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        holder.itemView.setAlpha(0);
        mAddHolder.add(holder);
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        Log.d("test", "animationMove,fromX" + fromX + "，fromY=" + fromY);
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        if (!mRemoveHolder.isEmpty()){
            List<RecyclerView.ViewHolder> mLists = new ArrayList<>();
            mLists.addAll(mRemoveHolder);
            for (RecyclerView.ViewHolder viewHolder : mLists) {
                mRemoveAnimations.add(viewHolder);
                View view = viewHolder.itemView;
                ViewPropertyAnimator animate = view.animate();
                animate.alpha(0)
                        .setDuration(500)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                dispatchRemoveStarting(viewHolder);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animate.setListener(null);
                                viewHolder.itemView.setAlpha(1);
                                dispatchRemoveFinished(viewHolder);
                                mRemoveAnimations.remove(viewHolder);
                                if (isRunning()) {
                                    dispatchAnimationsFinished();
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                viewHolder.itemView.setAlpha(1);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
            }
            mRemoveHolder.removeAll(mLists);
            mLists.clear();
        }

        if (!mAddHolder.isEmpty()) {
            List<RecyclerView.ViewHolder> mLists = new ArrayList<>();
            mLists.addAll(mAddHolder);
            for (RecyclerView.ViewHolder viewHolder : mLists) {
                mAddAnimations.add(viewHolder);
                View view = viewHolder.itemView;
                ViewPropertyAnimator animate = view.animate();
                animate.alpha(1)
                        .setDuration(500)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                dispatchAddStarting(viewHolder);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animate.setListener(null);
                                dispatchAddFinished(viewHolder);
                                mAddAnimations.remove(viewHolder);
                                if (isRunning()) {
                                    dispatchAnimationsFinished();
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                view.setAlpha(1);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                animate.start();
            }
            mAddHolder.removeAll(mLists);
            mLists.clear();
        }
    }

    @Override
    public void endAnimation(@NonNull RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return mAddHolder.size() > 0 || mAddAnimations.size() > 0;
    }

    public void resetAnimation(RecyclerView.ViewHolder viewHolder) {
        if (!mAddAnimations.isEmpty()) {
            for (RecyclerView.ViewHolder holder : mAddAnimations) {
                holder.itemView.animate().cancel();
            }
        }
    }
}
