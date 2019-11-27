package com.oyf.codecollection.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @创建者 oyf
 * @创建时间 2019/11/16 9:28
 * @描述
 **/

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected List<T> mInfos;
    protected OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private BaseViewHolder mHolder;
    private int resId = -1;

    public BaseAdapter(List<T> infos) {
        super();
        this.mInfos = infos;
    }

    public BaseAdapter(List<T> infos, int resId) {
        super();
        this.mInfos = infos;
        this.resId = resId;
    }

    /**
     * 创建
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = null;
        if (resId == -1) {
            view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
        }

        mHolder = getViewHolder(view);
        mHolder.setOnItemClickListener(new BaseViewHolder.OnViewClickListener() {//设置Item点击事件
            @Override
            public void onViewClick(View view, int position) {
                if (mInfos == null) {
                    return;
                }
                if (mOnItemClickListener != null && mInfos.size() > 0) {
                    mOnItemClickListener.onItemClick(view, viewType, mInfos.get(position), position);
                }
            }
        });
        return mHolder;
    }

    public BaseViewHolder getViewHolder(View v) {
        return new BaseViewHolder(v);
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (mInfos == null) {
            return;
        }
        convert(holder, mInfos.get(position));
    }

    protected abstract void convert(BaseViewHolder helper, T data);

    /**
     * 返回数据的个数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (mInfos == null) {
            return 0;
        }
        return mInfos.size();
    }


    public List<T> getInfos() {
        return mInfos;
    }

    /**
     * 获得某个  position 上的 item 的数据
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return mInfos == null ? null : mInfos.get(position);
    }

    /**
     * 提供用于  item 布局的  layoutId
     *
     * @param viewType
     * @return
     */
    public int getLayoutId(int viewType) {
        return -1;
    }


    /**
     * 遍历所有 BaseHolder,释放他们需要释放的资源
     *
     * @param recyclerView
     */
    public static void releaseAllHolder(RecyclerView recyclerView) {
        if (recyclerView == null) return;
        for (int i = recyclerView.getChildCount() - 1; i >= 0; i--) {
            final View view = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder != null && viewHolder instanceof BaseViewHolder) {
                ((BaseViewHolder) viewHolder).onRelease();
            }
        }
    }


    public interface OnRecyclerViewItemClickListener<T> {
        void onItemClick(View view, int viewType, T data, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
