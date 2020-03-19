package com.oyf.basemodule.weight.recycleview;

import android.view.View;

import java.util.Stack;

public class MyRecycle {
    private Stack<View>[] stacks;

    public MyRecycle(int num) {
        stacks = new Stack[num];
        for (int i = 0; i < num; i++) {
            stacks[i] = new Stack<>();
        }
    }

    public void put(View view, int viewType) {
        stacks[viewType].push(view);
    }

    public View get(int viewType) {
        try {
            return stacks[viewType].pop();
        } catch (Exception e) {
            return null;
        }
    }
}
