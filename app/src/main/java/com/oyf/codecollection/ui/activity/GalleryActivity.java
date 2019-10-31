package com.oyf.codecollection.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.R;

public class GalleryActivity extends BaseActivity {

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gallery;
    }

    private ImageSwitcher imageSwitcher;
    private Gallery gallery;
    private int images[] = {R.drawable.poster1,
            R.drawable.poster2, R.drawable.poster3};

    private int currentPosition;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        gallery = findViewById(R.id.g_img);
        imageSwitcher = findViewById(R.id.is_img);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {

                ImageView imageView = new ImageView(GalleryActivity.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 50;
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(params);
                return imageView;
            }
        });
        //imageSwitcher.setImageResource(images[currentPosition]);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        TranslateAnimation outAnimation = new TranslateAnimation(0, defaultDisplay.getWidth(), 0, 0);
        outAnimation.setDuration(500);
        imageSwitcher.setOutAnimation(outAnimation);
        TranslateAnimation inAnimation = new TranslateAnimation(-defaultDisplay.getWidth(), 0, 0, 0);
        inAnimation.setDuration(500);
        imageSwitcher.setInAnimation(inAnimation);
        gallery.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return images.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView = new ImageView(parent.getContext());
                Gallery.LayoutParams params = new Gallery.LayoutParams(200, 200);
                imageView.setLayoutParams(params);
                imageView.setImageResource(images[position]);
                return imageView;
            }
        });
        //gallery.setSelection(currentPosition);
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                imageSwitcher.setImageResource(images[currentPosition]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);

    }
}
