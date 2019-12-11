package com.oyf.basemodule.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * 输入密码显示框
 */
public class InputPassWordView extends View {
    public static final int STYLE_POINT = 0;
    public static final int STYLE_DEFAULT = 1;

    public InputPassWordView(Context context) {
        this(context, null);
    }

    public InputPassWordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputPassWordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint mPaintRect;
    int screenW;//屏幕的宽
    int screenH;//屏幕的高
    int leftPadding;
    int topPadding;
    int length;
    String pwd[];//输入密码的数据
    int width;//每个控件的宽
    int height;//每个控件的高
    int inputShowType;//显示在中间的类型，原点或者*号
    RectF rectf;
    Rect rect;
    int currentNumber = 0;
    boolean isShow = false;

    private void init() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenW = displayMetrics.widthPixels;
        screenH = displayMetrics.heightPixels;
        leftPadding = 100;
        topPadding = 100;
        pwd = new String[6];
        resetPwd();
        inputShowType = STYLE_POINT;
        length = 6;
        mPaintRect = new Paint();
        mPaintRect.setAntiAlias(true);
        mPaintRect.setStyle(Paint.Style.FILL);
        rectf = new RectF();
        rect = new Rect();
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = (w - leftPadding * 2) / length;
        height = width;
        rectf.left = leftPadding;
        rectf.top = topPadding;
        rectf.right = rectf.left + width * length;
        rectf.bottom = rectf.top + height;
        rect.set((int) rectf.left, (int) rectf.top, (int) rectf.right, (int) rectf.bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);
        drawPwdRect(canvas);
        drawPwd(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Region region = new Region();
            region.set(rect);
            boolean contains = region.contains((int) event.getX(), (int) event.getY());
            if (contains && !isShow) {
                showOrHide(getContext());
            }
            if (!contains && isShow) {
                showOrHide(getContext());
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                deletePwd();
                break;
            case KeyEvent.KEYCODE_1:
                addPwd("1");
                break;
            case KeyEvent.KEYCODE_2:
                addPwd("2");
                break;
            case KeyEvent.KEYCODE_3:
                addPwd("3");
                break;
            case KeyEvent.KEYCODE_4:
                addPwd("4");
                break;
            case KeyEvent.KEYCODE_5:
                addPwd("5");
                break;
            case KeyEvent.KEYCODE_6:
                addPwd("6");
                break;
            case KeyEvent.KEYCODE_7:
                addPwd("7");
                break;
            case KeyEvent.KEYCODE_8:
                addPwd("8");
                break;
            case KeyEvent.KEYCODE_9:
                addPwd("9");
                break;
            case KeyEvent.KEYCODE_0:
                addPwd("0");
                break;
        }
        return true;
    }

    private void drawPwdRect(Canvas canvas) {
        mPaintRect.setColor(Color.WHITE);

        canvas.drawRoundRect(rectf, 10, 10, mPaintRect);
        for (int i = 1; i < length; i++) {
            mPaintRect.setColor(Color.GRAY);
            mPaintRect.setStrokeWidth(2);
            canvas.drawLine(leftPadding + (i * width),
                    topPadding,
                    leftPadding + (i * width),
                    topPadding + height,
                    mPaintRect);
        }
    }

    private void drawPwd(Canvas canvas) {
        if (inputShowType == STYLE_POINT) {
            for (int i = 0; i < pwd.length; i++) {
                if (!TextUtils.isEmpty(pwd[i])) {
                    mPaintRect.setColor(Color.BLACK);
                    mPaintRect.setStrokeWidth(20);
                    mPaintRect.setStrokeCap(Paint.Cap.ROUND);
                    canvas.drawPoint((float) ((leftPadding + (i + 0.5) * width)), (topPadding + height / 2), mPaintRect);
                }
            }
        }
    }

    public void resetPwd() {
        for (int i = 0; i < pwd.length; i++) {
            pwd[i] = "";
        }
    }

    public void addPwd(String num) {
        if (currentNumber == length) {
            return;
        }
        pwd[currentNumber] = num;
        currentNumber++;
        invalidate();
        if (currentNumber == length) {
            showToast("已经输入完成,密码为" + getPwd());
            showOrHide(getContext());
        }
        Log.d("test", "pwd=" + getPwd() + ",curr=" + currentNumber);
    }

    public void deletePwd() {
        if (currentNumber == 0) {
            return;
        }
        currentNumber--;
        pwd[currentNumber] = "";
        invalidate();
        Log.d("test", "pwd=" + getPwd() + ",curr=" + currentNumber);
    }

    public String getPwd() {
        StringBuilder sb = new StringBuilder();
        for (String i : pwd) {
            if (!TextUtils.isEmpty(i)) {
                sb.append(i);
            } else {
                return sb.toString();
            }
        }
        return sb.toString();
    }

    public void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    //如果输入法在窗口上已经显示，则隐藏，反之则显示
    public void showOrHide(Context context) {
        if (isShow) {
            isShow = false;
        } else {
            isShow = true;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
