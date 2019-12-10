package com.oyf.plugin.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.oyf.plugin.ProxyActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @创建者 oyf
 * @创建时间 2019/12/10 14:50
 * @描述
 **/
public class PluginUtils {
    private static final String KEY_INTENT = "key_intent";

    public static void hookIActivityManager() {
        try {
            //获取activityManager中的IActivityManagerSingleton对象，用于取出singleton中的iActivityManager
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            Field iActivityManagerSingletonField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            iActivityManagerSingletonField.setAccessible(true);
            Object IActivityManagerSingleton = iActivityManagerSingletonField.get(null);

            //获取singleton的get方法，用于获取iactivitymanager接口
            Class<?> singltonClass = Class.forName("android.util.Singleton");
            Method get = singltonClass.getDeclaredMethod("get");
            get.setAccessible(true);
            //获取iActivityManager的对象
            final Object iActivityManager = get.invoke(IActivityManagerSingleton);
            //动态代理
            Object proxyIActivityManager = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{iActivityManager.getClass()}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if ("startActivity".equals(method.getName())) {
                                //获取原来的oldintent
                                Intent pluginIntent = (Intent) args[2];
                                Log.d("test", "pluginIntent拦截方法中，method=" + method.getName() +
                                        ",intent.pck==" + pluginIntent.getComponent().getPackageName() +
                                        ",classname=" + pluginIntent.getComponent().getClassName());
                                //创建新的代理proxyIntent
                                Intent proxyIntent = new Intent();
                                proxyIntent.setClassName("com.oyf.codecollection", ProxyActivity.class.getName());
                                //将老的intent保存到代理proxyIntent中
                                proxyIntent.putExtra(KEY_INTENT, pluginIntent);
                                //替换原来的oldIntent
                                args[2] = proxyIntent;
                                Log.d("test", "proxyInten拦截方法中，method=" + method.getName() +
                                        ",intent.pck==" + proxyIntent.getComponent().getPackageName() +
                                        ",classname=" + proxyIntent.getComponent().getClassName());
                            }
                            return method.invoke(iActivityManager, args);
                        }
                    });
            //找到singltton里面的iactivityManager，然后将代理的赋值进去
            Field mInstanceField = singltonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            mInstanceField.set(IActivityManagerSingleton, proxyIActivityManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookHandler() {
        try {
            //获取activityThreadClass
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThreadField.get(null);

            //获取里面的handler
            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Object mH = mHField.get(sCurrentActivityThread);
            //获取handler中的callback，然后设置一个callback
            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            //设置自己的callBack
            mCallbackField.set(mH, new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    if (msg.what == 100) {
                        try {
                            Log.d("test", "callback的handleMessage=" + msg.what);
                            //反射获取ActivityClientRecord
                            Field intentField = msg.obj.getClass().getDeclaredField("intent");
                            intentField.setAccessible(true);
                            //获取代理的intent
                            Intent proxyIntent = (Intent) intentField.get(msg.obj);
                            Intent pluginIntent = proxyIntent.getParcelableExtra(KEY_INTENT);
                            //将我们的需要打开的activity的重新复制上去
                            proxyIntent.setComponent(pluginIntent.getComponent());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //返回false，还需要执行handler内部的handlerMessage
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
