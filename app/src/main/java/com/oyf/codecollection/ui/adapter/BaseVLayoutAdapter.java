package com.oyf.codecollection.ui.adapter;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.alibaba.android.vlayout.DelegateAdapter;

import java.util.List;

public abstract class BaseVLayoutAdapter<T> extends DelegateAdapter.Adapter<BaseViewHolder> {

    private List<T> data;
    private int layoutId;

    public BaseVLayoutAdapter(int layoutId, List<T> data) {
        this.data = data;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new BaseViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        convert(holder, data.get(position));
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    protected abstract void convert(BaseViewHolder holder, T item);
}
