package com.oyf.aspectj;

import android.util.Log;

import com.oyf.aspectj.annotation.CheckLogin;
import com.oyf.aspectj.annotation.LimitClick;

/**
 * @创建者 oyf
 * @创建时间 2019/11/27 15:20
 * @描述
 **/
public class TestUtils {

    public static void click() {
        Log.d("test", "click");
    }

    public static void click1() {
        Log.d("test", "TestUtils.click1");
    }

    public static void click2() {
        Log.d("test", "TestUtils.click2");
    }

    public static int click3() {
        Log.d("test", "TestUtils.click3");
        return 0;
    }

    public static void abc() {
        Log.d("test", "TestUtils.abc");
    }
}
