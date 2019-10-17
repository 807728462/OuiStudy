package com.oyf.basemodule.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import com.oyf.basemodule.R;
import com.oyf.basemodule.bean.ProvinceItemBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.security.DomainCombiner;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ChinaMapView extends View {

    private int[] colorArray = new int[]{0xFF239BD7, 0xFF30A9E5, 0xFF80CBF1, 0xFFFFFFFF};
    List<ProvinceItemBean> mLists = new ArrayList<>();
    private Paint mPaint;
    private RectF mMapRectf;
    private float mScale;

    public ChinaMapView(Context context) {
        this(context, null);
    }

    public ChinaMapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChinaMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                float left = -1, top = -1, right = -1, bottom = -1;
                final InputStream inputStream = getContext().getResources().openRawResource(R.raw.china);
                //使用dom解析
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = null;
                try {
                    documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document parse = documentBuilder.parse(inputStream);
                    //先获取跟布局的属性
                    Element rootElement = parse.getDocumentElement();
                    NodeList paths = rootElement.getElementsByTagName("path");
                    for (int i = 0; i < paths.getLength(); i++) {
                        Element item = (Element) paths.item(i);
                        String pathData = item.getAttribute("android:pathData");
                        @SuppressLint("RestrictedApi") Path path = PathParser.createPathFromPathData(pathData);
                        ProvinceItemBean provinceItemBean = new ProvinceItemBean();
                        provinceItemBean.mPath = path;
                        provinceItemBean.isSelect = false;
                        provinceItemBean.color = colorArray[i % 4];
                        //为了计算整个地图的left，top，right，bottom
                        RectF rect = new RectF();
                        path.computeBounds(rect, true);
                        left = left == -1 ? rect.left : Math.min(left, rect.left);
                        right = right == -1 ? rect.right : Math.max(right, rect.right);
                        top = top == -1 ? rect.top : Math.min(top, rect.top);
                        bottom = bottom == -1 ? rect.bottom : Math.max(bottom, rect.bottom);
                        mLists.add(provinceItemBean);
                    }
                    //赋值整个map的宽高
                    mMapRectf = new RectF(left, top, right, bottom);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //刷新布局，重新计算mapview的宽高,进行缩放地图
                            requestLayout();
                            //重新绘制
                            invalidate();
                        }
                    });
                } catch (Exception e) {

                }
            }
        }).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mMapRectf != null) {
            double mapWidth = mMapRectf.width();
            mScale = (float) (width / mapWidth);
        }
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLists != null) {
            canvas.save();
            canvas.scale(mScale, mScale);
            for (ProvinceItemBean mList : mLists) {
                mList.draw(canvas, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mLists != null) {
                for (ProvinceItemBean mList : mLists) {
                    mList.isSelect = mList.isContain(event.getX() / mScale, event.getY() / mScale);
                }
            }
            invalidate();
        }
        return super.onTouchEvent(event);
    }
}
