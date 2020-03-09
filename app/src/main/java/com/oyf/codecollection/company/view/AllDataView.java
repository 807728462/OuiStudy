package com.oyf.codecollection.company.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.oyf.codecollection.R;

public class AllDataView extends LinearLayout {
    public AllDataView(Context context) {
        this(context,null);
    }

    public AllDataView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AllDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private CountDataView dv11;
    private CountDataView dv12;
    private CountDataView dv21;
    private CountDataView dv22;
    private CountDataView dv23;

    private void init(Context context) {
        setOrientation(VERTICAL);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_mine_statistics, null, false);
        dv11 = view.findViewById(R.id.dv_11);
        dv12 = view.findViewById(R.id.dv_12);
        dv21 = view.findViewById(R.id.dv_21);
        dv22 = view.findViewById(R.id.dv_22);
        dv23 = view.findViewById(R.id.dv_23);
        addView(view);
    }



}
