package com.oyf.basemodule.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

public class AdapterTextSizeUtil {
    @SuppressLint("NewApi")
    public static void updateTextSize(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                updateTextSize(childAt);
            }
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setAutoSizeTextTypeUniformWithPresetSizes(new int[]{10, 12, 14, 16, 18, 20, 22, 24, 26}, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
    }
}
