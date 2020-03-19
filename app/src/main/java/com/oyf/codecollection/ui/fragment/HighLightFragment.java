package com.oyf.codecollection.ui.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.oyf.codecollection.R;
import com.oyf.codecollection.manager.HighlightManager;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.position.OnLeftPosCallback;
import zhy.com.highlight.shape.RectLightShape;

/**
 * @创建者 oyf
 * @创建时间 2020/3/11 11:14
 * @描述
 **/
public class HighLightFragment extends Fragment {

    private View inflate;
    private Button bt_fragment;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = LayoutInflater.from(getContext()).inflate(R.layout.fragment_highlight, null);
        initView();
        return inflate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        bt_fragment = inflate.findViewById(R.id.bt_fragment);
        bt_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHighLight();
            }
        });
    }

    private void showHighLight() {
        HighLight mHightLight = HighlightManager.getInstance().creat(getActivity());
        mHightLight.addHighLight(bt_fragment, R.layout.layout_highlight_top, new OnLeftPosCallback(45), new RectLightShape());
        mHightLight.show();
    }
}
