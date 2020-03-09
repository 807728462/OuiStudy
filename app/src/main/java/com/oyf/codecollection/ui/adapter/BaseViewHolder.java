package com.oyf.codecollection.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;


public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final SparseArray<View> views;
    private View convertView;
    protected OnViewClickListener mOnViewClickListener = null;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.views = new SparseArray<>();
        convertView = itemView;
        convertView.setOnClickListener(this);
    }

    //根据id检索获得该View对象
    private <T extends View> T retrieveView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public BaseViewHolder setText(int viewId, CharSequence charSequence) {
        TextView textView = retrieveView(viewId);
        textView.setText(charSequence);
        return this;
    }

    @Override
    public void onClick(View view) {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onViewClick(view, this.getPosition());
        }
    }

    public interface OnViewClickListener {
        void onViewClick(View view, int position);
    }

    public void setOnItemClickListener(OnViewClickListener listener) {
        this.mOnViewClickListener = listener;
    }


    protected void onRelease() {

    }

    public <T extends View> T getView(int viewId) {
        return retrieveView(viewId);
    }

}
